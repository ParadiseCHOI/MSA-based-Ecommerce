package com.example.catalogservice.service;

import com.example.catalogservice.dto.CatalogDto;
import com.example.catalogservice.reopository.CatalogEntity;
import com.example.catalogservice.reopository.CatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService{
    CatalogRepository repository;
    Environment env;

    public CatalogServiceImpl(CatalogRepository repository, Environment env) {
        this.repository = repository;
        this.env = env;
    }

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return repository.findAll() ;
    }

    @Override
    public CatalogDto getCatalogByProductId(String productId) {
        /*
        Optional<CatalogEntity> catalogEntity = repository.findByProductId(productId);

        if(catalogEntity.isEmpty()) {
            return null;
        }

        CatalogDto catalogDto = new ModelMapper().map(catalogEntity.get(), CatalogDto.class);
        */
        return null;
    }
}
