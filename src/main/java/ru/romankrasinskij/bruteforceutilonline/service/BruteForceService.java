package ru.romankrasinskij.bruteforceutilonline.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BruteForceService {

    private final UserService userService;

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789" +
            "!@#$%^&*()_+-=[]{}|;:'\",.<>/?`~ ";

    public BruteForceService(UserService userService) {
        this.userService = userService;
    }

    private List<String> readLines(String fileName) throws Exception {
        List<String> lines = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(fileName);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        }
        return lines;
    }

    public String bruteForceLogin() throws Exception {
        List<String> logins = readLines("logins.txt");
        int total = logins.size();
        long startTime = System.currentTimeMillis();
        List<String> results = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            String currentLogin = logins.get(i);
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;

            System.out.printf("\r[File-Login] %d из %d '%s' | Прошло: %d сек",
                    (i + 1), total, currentLogin, elapsedSeconds);

            if (userService.checkLoginExists(currentLogin)) {
                long duration = System.currentTimeMillis() - startTime;
                results.add(String.format("Логин '%s' найден за %d ms", currentLogin, duration));
            }
        }
        System.out.println();

        if (results.isEmpty()) {
            return "Совпадений по логинам в словаре не найдено.";
        }
        return String.join("\n", results);
    }

    public String bruteForcePassword(String targetLogin) throws Exception {
        List<String> passwords = readLines("passwords.txt");
        int total = passwords.size();
        long startTime = System.currentTimeMillis();
        List<String> results = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            String currentPassword = passwords.get(i);
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;

            System.out.printf("\r[File-Pass] %d из %d '%s' | Прошло: %d сек",
                    (i + 1), total, currentPassword, elapsedSeconds);

            if (userService.authenticate(targetLogin, currentPassword)) {
                long duration = System.currentTimeMillis() - startTime;
                results.add(String.format("Пароль '%s' для пользователя '%s' найден за %d ms",
                        currentPassword, targetLogin, duration));
            }
        }
        System.out.println();

        if (results.isEmpty()) {
            return String.format("Пароль для пользователя '%s' не найден в словаре.", targetLogin);
        }
        return String.join("\n", results);
    }

    public String generateAndBruteForceLogin(int minLength, int maxLength) {
        long startTime = System.currentTimeMillis();
        long[] attempts = new long[] { 0 };
        List<String> results = new ArrayList<>();

        for (int length = minLength; length <= maxLength; length++) {
            bruteForceLoginByLength("", length, startTime, attempts, results);
        }

        if (results.isEmpty()) {
            return String.format("Логинов не найдено среди сгенерированных комбинаций длиной от %d до %d символов.",
                    minLength, maxLength);
        }
        return String.join("\n", results);
    }

    private void bruteForceLoginByLength(String current, int targetLength, long startTime, long[] attempts,
            List<String> results) {
        if (current.length() == targetLength) {
            attempts[0]++;
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;

            System.out.printf("\r[Gen-Login] Попытка %d '%s' | Прошло: %d сек",
                    attempts[0], current, elapsedSeconds);

            if (userService.checkLoginExists(current)) {
                long duration = System.currentTimeMillis() - startTime;
                results.add(String.format("Логин '%s' найден за %d ms (попытка №%d)",
                        current, duration, attempts[0]));
            }
            return;
        }

        for (int i = 0; i < CHARSET.length(); i++) {
            bruteForceLoginByLength(current + CHARSET.charAt(i), targetLength, startTime, attempts, results);
        }
    }

    public String generateAndBruteForcePassword(String targetLogin, int minLength, int maxLength) {
        long startTime = System.currentTimeMillis();
        long[] attempts = new long[] { 0 };

        for (int length = minLength; length <= maxLength; length++) {
            String foundPassword = bruteForcePasswordByLength("", length, targetLogin, startTime, attempts);
            if (foundPassword != null) {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println();
                return String.format("Пароль '%s' для пользователя '%s' найден за %d ms (всего попыток: %d)",
                        foundPassword, targetLogin, duration, attempts[0]);
            }
        }

        System.out.println();
        return String.format("Пароль для пользователя '%s' не найден среди комбинаций длиной от %d до %d символов.",
                targetLogin, minLength, maxLength);
    }

    private String bruteForcePasswordByLength(String current, int targetLength, String targetLogin, long startTime,
            long[] attempts) {
        if (current.length() == targetLength) {
            attempts[0]++;
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;

            System.out.printf("\r[Gen-Pass] Попытка %d '%s' | Прошло: %d сек",
                    attempts[0], current, elapsedSeconds);

            if (userService.authenticate(targetLogin, current)) {
                return current;
            }
            return null;
        }

        for (int i = 0; i < CHARSET.length(); i++) {
            String result = bruteForcePasswordByLength(current + CHARSET.charAt(i), targetLength, targetLogin,
                    startTime, attempts);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}