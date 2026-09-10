package ru.romankrasinskij.bruteforceutilonline.dto;

import ru.romankrasinskij.bruteforceutilonline.entity.User;

public class UserResponseDto {

    private Long id;
    private String login;
    private String firstName;
    private String lastName;

    public UserResponseDto(Long id, String login, String firstName, String lastName) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName());
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}