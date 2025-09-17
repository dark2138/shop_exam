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
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public static final int MIN_MY_PRICE = 100;

    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = new Product(requestDto, user);
        productRepository.save(product);
        return new ProductResponseDto(product);
    }
    
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        
        int myprice = requestDto.getMyprice();

        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 최저가 입니다. 최소 최저가는 " + MIN_MY_PRICE + "원 이상으로 설정해주세요.");
        }
        
        Product product = productRepository.findById(id).orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));
    
        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleEnum role = user.getRole();

        Page<Product> productList;

        if (role == UserRoleEnum.ADMIN) {
            productList = productRepository.findAll(pageable);
        } else {
            productList = productRepository.findAllByUser(user, pageable);
        }

        return productList.map(ProductResponseDto::new);
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));
        product.updateByItemDto(itemDto);
    }
}
