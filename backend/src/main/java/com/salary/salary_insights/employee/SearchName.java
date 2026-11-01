package com.salary.salary_insights.employee;

import java.text.Normalizer;
import java.util.Locale;

// The single form a name is stored and searched in: no accents, lower case.
public final class SearchName {

    private SearchName() {
    }

    public static String normalize(String text) {
        // NFD splits "é" into "e" + accent; \p{M} removes the accent.
        // "ß" is a letter of its own, not an accented "s", so it needs its own rule.
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("ß", "ss");
        return withoutAccents.toLowerCase(Locale.ROOT);
    }
}
