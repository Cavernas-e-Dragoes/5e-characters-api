package com.ced.controller;


import com.ced.dto.UserDTO;
import com.ced.model.User;
import com.ced.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String version() {
        return "User API 1.2";
    }

    @CrossOrigin(origins = {"https://cavernasedragoes.com.br", "http://localhost:4200"})
    @PostMapping("/create")
    public ResponseEntity<UserDTO> create(@RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.userCreate(user));
    }
}
