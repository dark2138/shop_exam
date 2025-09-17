package com.sparta.myselectshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.myselectshop.entity.Folder;
import org.springframework.stereotype.Repository;
@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
}
