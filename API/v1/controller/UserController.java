package capston.capston_spring.controller;

import capston.capston_spring.dto.EditNameRequest;
import capston.capston_spring.dto.EditPasswordRequest;
import capston.capston_spring.dto.UserResponseDto;
import capston.capston_spring.service.AccuracySessionService;
import capston.capston_spring.service.UserService;
import capston.capston_spring.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AccuracySessionService accuracySessionService;
    private final VideoService videoService;


    /** 사용자 정보 조회 (프로필, 영상 목록, 점수 기록 포함) **/
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getUserProfile(Authentication authentication) {
        Long userId = userService.getUserId(authentication);
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }


    /** 사용자 정보 수정 (이름 변경) **/
    @PatchMapping("/profile/edit/name")
    public ResponseEntity<String> editProfileName(Authentication authentication, @RequestBody @Valid EditNameRequest request, BindingResult bindingResult) {
        Long userId = userService.getUserId(authentication);
        userService.updateName(userId, request.getName());
        return ResponseEntity.ok("이름이 성공적으로 수정되었습니다.");
    }

    /** 비밀번호 변경 **/
    @PatchMapping("/profile/edit/password")
    public ResponseEntity<String> editProfilePassword(Authentication authentication, @RequestBody @Valid EditPasswordRequest request, BindingResult bindingResult) {
        try {
            Long userId = userService.getUserId(authentication);
            userService.updatePassword(userId, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    /** 프로필 이미지 변경 **/
    @PatchMapping(value = "/profile/edit/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editProfileImg(Authentication authentication, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }
        Long userId = userService.getUserId(authentication);
        userService.updateProfileImage(userId, file);
        return ResponseEntity.ok("프로필 이미지가 성공적으로 수정되었습니다.");
    }

    /** 계정 삭제 **/
    @DeleteMapping("/profile/deleteAccount")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        Long userId = userService.getUserId(authentication);
        userService.deleteAccount(userId);
        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
    }


}
