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
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.entity.ProductFolder;

import java.util.Optional;

import com.sparta.myselectshop.repository.ProductFolderRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;

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

    @Transactional
    public void addProductToFolder(Long productId, Long folderId, User user) {
        System.out.println("DEBUG: addProductToFolder 시작 - productId: " + productId + ", folderId: " + folderId + ", userId: " + user.getId());
        
        Product product = productRepository.findById(productId).orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new NullPointerException("폴더를 찾을 수 없습니다."));

        System.out.println("DEBUG: product.userId = " + product.getUser().getId() + ", folder.userId = " + folder.getUser().getId());

        if (!product.getUser().getId().equals(user.getId())
                || !folder.getUser().getId().equals(user.getId())
        ) {
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 폴더 소유자가 아닙니다.");
        }

        System.out.println("DEBUG: 권한 확인 완료 - productId = " + product.getId() + ", folderId = " + folder.getId());
        Optional<ProductFolder> overlapProductFolder = productFolderRepository.findByProductAndFolder(product, folder);
        System.out.println("DEBUG: overlapProductFolder.isPresent() = " + overlapProductFolder.isPresent());
        if (overlapProductFolder.isPresent()) {
            System.out.println("DEBUG: 중복된 ProductFolder 발견: " + overlapProductFolder.get().getId());
            throw new IllegalArgumentException("중복된 폴더입니다.");
        }

        System.out.println("DEBUG: ProductFolder 저장 시작");
        productFolderRepository.save(new ProductFolder(product, folder));
        System.out.println("DEBUG: ProductFolder 저장 완료");
    }
}
