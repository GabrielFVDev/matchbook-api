package com.matchbook.matchbook.services;

import com.matchbook.matchbook.models.UserPreferencesModel;
import com.matchbook.matchbook.repositories.UserPreferencesRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;

    public UserPreferencesService(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    public Optional<UserPreferencesModel> getPreferences(String firebaseUid) {
        return userPreferencesRepository.findById(firebaseUid);
    }

    // Creates or replaces. The uid comes from the token, never from the request body.
    public UserPreferencesModel savePreferences(String firebaseUid, UserPreferencesModel preferences) {
        preferences.setFirebaseUid(firebaseUid);
        return userPreferencesRepository.save(preferences);
    }

    public void deletePreferences(String firebaseUid) {
        userPreferencesRepository.deleteById(firebaseUid);
    }
}
