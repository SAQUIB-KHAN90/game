package com.example.fastapicolorgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;

@SpringBootApplication
public class FastApiColorGameApplication {
    public static void main(String[] args) {
        SpringApplication.run(FastApiColorGameApplication.class, args);
    }
}

// Game Service
@RestController
@RequestMapping("/fast-api")
class ColorGameController {
    private static final List<String> COLORS = Arrays.asList("Red", "Blue", "Green");
    private static final Map<String, String> userColor = new HashMap<>();
    private static final Map<String, Integer> userScore = new HashMap<>();
    private static final Map<String, Integer> attemptsLeft = new HashMap<>();

    // Start the game for a user
    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestParam String user) {
        String randomColor = COLORS.get(new Random().nextInt(COLORS.size()));
        userColor.put(user, randomColor);
        userScore.put(user, 0);
        attemptsLeft.put(user, 5);
    }

    // User guesses a color
    @GetMapping("/guess/{color}")
    public ResponseEntity<String> guessColor(@RequestParam String user, @PathVariable String color) {
        if (!userColor.containsKey(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start the game first with /fast-api/start");
        }

        if (attemptsLeft.get(user) <= 0) {
            return ResponseEntity.ok("No attempts left! Reset the game to play again.");
        }

        String correctColor = userColor.get(user);
        if (correctColor.equalsIgnoreCase(color)) {
            userScore.put(user, userScore.get(user) + 10);
            return ResponseEntity.ok("Correct! Your score: " + userScore.get(user) + ". Start a new game to play again.");
        } else {
            attemptsLeft.put(user, attemptsLeft.get(user) - 1);
            return ResponseEntity.ok("Wrong! Attempts left: " + attemptsLeft.get(user));
        }
    }

    // Get user score
    @GetMapping("/score")
    public ResponseEntity<String> getScore(@RequestParam String user) {
        return userScore.containsKey(user) ?
                ResponseEntity.ok("Your score: " + userScore.get(user)) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start the game first!");
    }

    // Get remaining attempts
    @GetMapping("/attempts")
    public ResponseEntity<String> getAttemptsLeft(@RequestParam String user) {
        return attemptsLeft.containsKey(user) ?
                ResponseEntity.ok("Attempts left: " + attemptsLeft.get(user)) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start the game first!");
    }

    // Reset the game
    @PostMapping("/reset")
    public ResponseEntity<String> resetGame(@RequestParam String user) {
        userColor.remove(user);
        userScore.remove(user);
        attemptsLeft.remove(user);
        return ResponseEntity.ok("Game reset for " + user + ". Start a new game to play again.");
    }

    // Get list of available colors
    @GetMapping("/colors")
    public ResponseEntity<List<String>> getColors() {
        return ResponseEntity.ok(COLORS);
    }
}
