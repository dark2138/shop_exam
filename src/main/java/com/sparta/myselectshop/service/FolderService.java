package com.sparta.myselectshop.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.sparta.myselectshop.dto.FolderResponseDto;

@Service
@RequiredArgsConstructor
@Transactional
public class FolderService {

    private final FolderRepository folderRepository;

    public void createFolder(List<String> folderNames, User user) {

        List<Folder> existingFolders = folderRepository.findAllByUserAndNameIn(user, folderNames);


        List<Folder> newFolders = new ArrayList<>();

        for (String folderName : folderNames) {
            if (!isExistingFolderName(existingFolders, folderName)) {
                Folder folder = new Folder(folderName, user);
                newFolders.add(folder);
            } else {
                throw new IllegalArgumentException("중복된 폴더명입니다.");
            }
        }


        folderRepository.saveAll(newFolders);
    }


    public List<FolderResponseDto> getUserFolders(User user) {
        List<Folder> folders = folderRepository.findAllByUser(user);
        return folders.stream().map(FolderResponseDto::new).collect(Collectors.toList());
    }



    private boolean isExistingFolderName(List<Folder> existingFolders, String folderName) {
        return existingFolders.stream().anyMatch(folder -> folder.getName().equals(folderName));
    }
    
}
