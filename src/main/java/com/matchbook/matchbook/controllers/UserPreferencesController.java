package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.models.UserPreferencesModel;
import com.matchbook.matchbook.services.UserPreferencesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

// The logged-in user's preferences. The uid is the "sub" of the Firebase token.
@RestController
@RequestMapping("/api/me/preferences")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    public UserPreferencesController(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
    }

    @GetMapping
    public ResponseEntity<UserPreferencesModel> getPreferences(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.of(userPreferencesService.getPreferences(token.getSubject()));
    }

    @PutMapping
    public ResponseEntity<UserPreferencesModel> savePreferences(
            @AuthenticationPrincipal Jwt token,
            @RequestBody UserPreferencesModel preferences
    ) {
        return ResponseEntity.ok(userPreferencesService.savePreferences(token.getSubject(), preferences));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePreferences(@AuthenticationPrincipal Jwt token) {
        userPreferencesService.deletePreferences(token.getSubject());
        return ResponseEntity.noContent().build();
    }
}
