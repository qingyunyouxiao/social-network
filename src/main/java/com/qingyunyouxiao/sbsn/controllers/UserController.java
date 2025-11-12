package com.qingyunyouxiao.sbsn.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qingyunyouxiao.sbsn.dtos.ProfileDto;
import com.qingyunyouxiao.sbsn.services.UserService;

@RestController
@RequestMapping("/v1/uers")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ProfileDto> getUserProfile(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addFriends(@PathVariable Long friendId) {

    }
    @PostMapping
    public ResponseEntity<> searchUsers(@RequestParam()) {

    }
}
