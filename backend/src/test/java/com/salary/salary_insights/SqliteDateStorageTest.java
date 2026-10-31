package com.salary.salary_insights;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A date written from one time zone must read back as the same day from any other.
 * Without date_class=TEXT in the datasource URL, the SQLite driver stores dates as
 * milliseconds in the writer's zone, and a server running in UTC reads a hire date
 * of 2024-01-15 written from Madrid as 2024-01-14.
 */
@SpringBootTest
class SqliteDateStorageTest {

    private static final LocalDate HIRE_DATE = LocalDate.of(2024, 1, 15);

    private final TimeZone originalZone = TimeZone.getDefault();

    @Value("${spring.datasource.url}")
    private String url;

    @AfterEach
    void restoreZone() {
        TimeZone.setDefault(originalZone);
    }

    @Test
    void dateWrittenFromMadridReadsAsTheSameDayInUtc() throws SQLException {
        // Each connection takes the JVM's zone when it opens, like two separate servers.
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));
        try (Connection writer = DriverManager.getConnection(url)) {
            execute(writer, "CREATE TABLE date_probe (d DATE)");
            try {
                try (PreparedStatement insert = writer.prepareStatement("INSERT INTO date_probe VALUES (?)")) {
                    insert.setDate(1, Date.valueOf(HIRE_DATE));
                    insert.executeUpdate();
                }

                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                try (Connection reader = DriverManager.getConnection(url);
                     Statement select = reader.createStatement();
                     ResultSet rs = select.executeQuery("SELECT d FROM date_probe")) {
                    rs.next();
                    assertThat(rs.getDate(1).toLocalDate()).isEqualTo(HIRE_DATE);
                }
            } finally {
                execute(writer, "DROP TABLE date_probe");
            }
        }
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
