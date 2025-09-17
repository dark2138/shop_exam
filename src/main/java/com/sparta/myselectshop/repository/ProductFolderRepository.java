package com.sparta.myselectshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.myselectshop.entity.ProductFolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.Folder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductFolderRepository extends JpaRepository<ProductFolder, Long> {
    Optional<ProductFolder> findByProductAndFolder(Product product, Folder folder);

}
