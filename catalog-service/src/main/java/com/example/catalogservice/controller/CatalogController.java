package com.example.catalogservice.controller;

import com.example.catalogservice.dto.CatalogDto;
import com.example.catalogservice.reopository.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/catalog-service")
public class CatalogController {
    Environment env;
    CatalogService catalogService;

    public CatalogController(Environment env, CatalogService catalogService) {
        this.env = env;
        this.catalogService = catalogService;
    }

    @GetMapping("/health_check")
    public String status() {
        // return "It's Working in User Service";
        return String.format("It's Working in Catalog Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getAllCatalogs() {
        Iterable<CatalogEntity> catalogList = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();

        catalogList.forEach(catalog -> result.add(mapper.map(catalog, ResponseCatalog.class)));

        return ResponseEntity.ok(result);
    }

    /* ************************************
     * 상품 상세 정보 조회
     * GET /catalog-service/catalogs/{productId}
     ************************************ */
    // @GetMapping("/catalogs/{productId}")
    // public ResponseEntity<ResponseCatalog> getCatalogByProductId(@PathVariable("productId") String productId) {
    //     CatalogDto catalog = catalogService.getUserByUserId(userId);
    //
    //     ResponseUser result = new ModelMapper().map(user, ResponseUser.class);
    //
    //     return ResponseEntity.ok(result);
    // }
}
