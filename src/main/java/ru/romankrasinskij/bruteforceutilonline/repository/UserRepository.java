package ru.romankrasinskij.bruteforceutilonline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romankrasinskij.bruteforceutilonline.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndPassword(String login, String password);
}
