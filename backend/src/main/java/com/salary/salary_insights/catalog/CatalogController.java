package com.salary.salary_insights.catalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
class CatalogController {

    @GetMapping
    Catalog catalog() {
        return new Catalog(List.of(), List.of(), List.of());
    }
}
