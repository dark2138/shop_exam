package com.sparta.myselectshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.myselectshop.entity.ProductFolder;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFolderRepository extends JpaRepository<ProductFolder, Long> {
    
}
