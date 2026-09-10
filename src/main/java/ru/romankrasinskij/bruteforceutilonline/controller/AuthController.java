package ru.romankrasinskij.bruteforceutilonline.controller;

import ru.romankrasinskij.bruteforceutilonline.dto.UserResponseDto;
import ru.romankrasinskij.bruteforceutilonline.entity.User;
import ru.romankrasinskij.bruteforceutilonline.service.BruteForceService;
import ru.romankrasinskij.bruteforceutilonline.service.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final BruteForceService bruteForceService;

    public AuthController(UserService userService, BruteForceService bruteForceService) {
        this.userService = userService;
        this.bruteForceService = bruteForceService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password) {
        boolean success = userService.authenticate(login, password);
        return success ? "Успешный вход" : "Неверный логин или пароль";
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/search")
    public ResponseEntity<UserResponseDto> getUserByLogin(@RequestParam String login) {
        return userService.getUserByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/attack/login")
    public String attackLogin() throws Exception {
        return bruteForceService.bruteForceLogin();
    }

    @GetMapping("/attack/password")
    public String attackPassword(@RequestParam String login) throws Exception {
        return bruteForceService.bruteForcePassword(login);
    }

    @GetMapping("/attack/both")
    public String attackBoth() throws Exception {
        return bruteForceService.bruteForceBoth();
    }

    @GetMapping("/attack/generate/login")
    public String attackGenerateLogin(@RequestParam(defaultValue = "8") int minLength,
            @RequestParam(defaultValue = "16") int maxLength) throws Exception {
        return bruteForceService.generateAndBruteForceLogin(minLength, maxLength);
    }

    @GetMapping("/attack/generate/password")
    public String attackGeneratePassword(@RequestParam String login,
            @RequestParam(defaultValue = "8") int minLength,
            @RequestParam(defaultValue = "16") int maxLength) throws Exception {
        return bruteForceService.generateAndBruteForcePassword(login, minLength, maxLength);
    }
}
