package com.qingyunyouxiao.sbsn.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.qingyunyouxiao.sbsn.dtos.ProfileDto;
import com.qingyunyouxiao.sbsn.services.UserService;

public class CommunityController {
    private final UserService userService;

    public CommunityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfileDto> getUserProfile(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PostMapping("/friends/friendId")
    public ResponseEntity<Void> addFriends(@PathVariable Long friendId) {
        return ();
    }

    @PostMapping
    public ResponseEntity<> searchUsers(@RequestParam()) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
