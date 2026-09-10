package ru.romankrasinskij.bruteforceutilonline.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import ru.romankrasinskij.bruteforceutilonline.repository.UserRepository;
import ru.romankrasinskij.bruteforceutilonline.dto.UserResponseDto;
import ru.romankrasinskij.bruteforceutilonline.entity.User;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return userRepository.save(user);
    }

    public boolean authenticate(String login, String password) {
        return userRepository.findByLoginAndPassword(login, password).isPresent();
    }

    public boolean checkLoginExists(String login) {
        return userRepository.findByLogin(login).isPresent();
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .toList();
    }

    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDto::fromEntity);
    }

    public Optional<UserResponseDto> getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(UserResponseDto::fromEntity);
    }
}
