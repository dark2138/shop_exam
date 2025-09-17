package com.sparta.myselectshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.service.ProductService;
import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import com.sparta.myselectshop.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return productService.createProduct(requestDto, userDetails.getUser());
    }

    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto) {
        return productService.updateProduct(id, requestDto);
    }

    @GetMapping("/products")
    public Page<ProductResponseDto> getProducts(
        @RequestParam(required = false, name = "page") int page,
        @RequestParam(required = false, name = "size") int size,
        @RequestParam(required = false, name = "sortBy") String sortBy,
        @RequestParam(required = false, name = "isAsc") boolean isAsc,
    @AuthenticationPrincipal UserDetailsImpl userDetails) {


        return productService.getProducts(userDetails.getUser(), page-1, size, sortBy, isAsc);
    }


    @GetMapping("/admin/products")
    public Page<ProductResponseDto> getAdminProducts(
            @RequestParam(required = false, name = "page") int page,
            @RequestParam(required = false, name = "size") int size,
            @RequestParam(required = false, name = "sortBy") String sortBy,
            @RequestParam(required = false, name = "isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return productService.getProducts(userDetails.getUser(), page-1, size, sortBy, isAsc);
    }
}
