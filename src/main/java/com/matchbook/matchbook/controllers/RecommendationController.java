package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.services.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Every route acts on the logged-in user: the "sub" of the Firebase token.
@RestController
@RequestMapping("/api/recommendation")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/getRecommendation")
    public ResponseEntity<List<BookModel>> getRecommendation(@AuthenticationPrincipal Jwt token) {
        List<BookModel> recommendations = recommendationService.getUserRecommendations(token.getSubject());
        if (recommendations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/getAdaptiveRecommendation")
    public ResponseEntity<List<BookModel>> getAdaptiveRecommendation(@AuthenticationPrincipal Jwt token) {
        List<BookModel> recommendations = recommendationService.getAdaptiveRecommendations(token.getSubject());
        if (recommendations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectBook(@AuthenticationPrincipal Jwt token, @RequestParam Integer bookId) {
        try {
            Map<String, Object> result = recommendationService.rejectBookWithAnalysis(token.getSubject(), bookId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptBook(@AuthenticationPrincipal Jwt token, @RequestParam Integer bookId) {
        try {
            recommendationService.acceptBook(token.getSubject(), bookId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@AuthenticationPrincipal Jwt token) {
        try {
            Map<String, Object> statistics = recommendationService.getRecommendationStatistics(token.getSubject());
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/resetAlgorithm")
    public ResponseEntity<Void> resetAlgorithm(@AuthenticationPrincipal Jwt token) {
        try {
            recommendationService.resetUserHistory(token.getSubject());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
