package com.sparta.myselectshop.service;

import com.sparta.myselectshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public static final int MIN_MYPRICE = 100;

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product(requestDto);
        productRepository.save(product);
        return new ProductResponseDto(product);
    }
    
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        
        int myprice = requestDto.getMyprice();

        if (myprice < MIN_MYPRICE) {
            throw new IllegalArgumentException("유효하지 않은 최저가 입니다. 최소 최저가는 " + MIN_MYPRICE + "원 이상으로 설정해주세요.");
        }
        
        Product product = productRepository.findById(id).orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));
    
        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProducts() {
        return productRepository.findAll().stream().map(ProductResponseDto::new).collect(Collectors.toList());
    }

}
