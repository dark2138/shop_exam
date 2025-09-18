package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.dto.UserInfoDto;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import com.sparta.myselectshop.service.FolderService;
import com.sparta.myselectshop.dto.FolderResponseDto;
import java.util.List;
import com.sparta.myselectshop.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestParam;
import com.sparta.myselectshop.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final FolderService folderService;
    private final KakaoService kakaoService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${server.base-url}")
    private String serverBaseUrl;

    @GetMapping("/user/login-page")
    public String loginPage(Model model) {
        // 카카오 로그인 URL 생성
        String fullRedirectUri = serverBaseUrl + kakaoRedirectUri;
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId + 
                              "&redirect_uri=" + fullRedirectUri + "&response_type=code";
        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRoleEnum role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRoleEnum.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }

    @GetMapping("/user-folder")
    public String getUserFolders(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<FolderResponseDto> folders = folderService.getUserFolders(userDetails.getUser());
        model.addAttribute("folders", folders);
        return "index :: #fragment";
    }

    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);
       
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";

    }
}