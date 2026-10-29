package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.models.UserPreferencesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferencesModel, String> {
}
