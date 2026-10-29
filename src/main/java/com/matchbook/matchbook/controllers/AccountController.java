package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Call before deleting the account in Firebase: afterwards the token no longer works
    @DeleteMapping
    public ResponseEntity<Void> deleteAccountData(@AuthenticationPrincipal Jwt token) {
        accountService.deleteAccountData(token.getSubject());
        return ResponseEntity.noContent().build();
    }
}
