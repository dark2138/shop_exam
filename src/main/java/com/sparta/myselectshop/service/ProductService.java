package com.sparta.myselectshop.service;

import com.sparta.myselectshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.entity.Product;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product(requestDto);
        productRepository.save(product);
        return new ProductResponseDto(product);
    }

}
