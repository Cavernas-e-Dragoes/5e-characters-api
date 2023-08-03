package com.ced.users.controller;

import com.ced.users.model.User;
import com.ced.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/api/user")
public class UserController {

    private final PasswordEncoder encoder;
    private final UserRepository repository;

    @Autowired
    public UserController(PasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @GetMapping("/")
    public String version(){
        return "1.2";
    }

    @CrossOrigin(origins = { "https://cavernasedragoes.com.br", "http://localhost:4200" })
    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(user));
    }
}
