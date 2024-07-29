package org.example.userservice.controller;


import jakarta.validation.Valid;
import org.example.userservice.Model.User;
import org.example.userservice.dto.AddUserRequest;
import org.example.userservice.enums.UserType;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user")
    public User createUser(@RequestBody @Valid AddUserRequest request){
        return userService.createUser(request);
    }
}
