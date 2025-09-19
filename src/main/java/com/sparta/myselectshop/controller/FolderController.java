package com.sparta.myselectshop.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.sparta.myselectshop.dto.FolderRequestDto;
import com.sparta.myselectshop.service.FolderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.sparta.myselectshop.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.sparta.myselectshop.exception.RestApiException;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import com.sparta.myselectshop.dto.FolderResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/folders")
    public void createFolder(@RequestBody FolderRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> folderNames = requestDto.getFolderNames();
        folderService.createFolder(folderNames, userDetails.getUser());


    }


    @GetMapping("/folders")
    public List<FolderResponseDto> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getUserFolders(userDetails.getUser());
    }


}
