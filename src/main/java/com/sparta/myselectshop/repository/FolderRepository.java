package com.sparta.myselectshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.myselectshop.entity.Folder;
import org.springframework.stereotype.Repository;
import com.sparta.myselectshop.entity.User;
import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);
    // select * from folder where user_id = ? and name in (?, ?, ?)

    List<Folder> findAllByUser(User user);
}
