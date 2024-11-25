package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.auth.UserSignInReq;
import com.uofc.acmeplex.dto.request.auth.UserSignUpDto;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserManagementController {

    private final IUserService userService;

    @PostMapping("/sign-in")
    public IResponse signInUser(@RequestBody UserSignInReq req) {
        return userService.signIn(req);
    }

    @PostMapping("/sign-out")
    public IResponse signOutUser() {
        return userService.signOut();
    }

    @PostMapping()
    public IResponse createUser(@RequestBody @Valid UserSignUpDto req) {
        return userService.createUser(req);
    }
}
