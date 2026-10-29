package com.matchbook.matchbook.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreferencesModel {
    @Id
    @Column(name = "firebase_uid", length = 128)
    private String firebaseUid;

    @Column(name = "favorite_genre")
    private String favoriteGenre;

    @Column(name = "favorite_author")
    private String favoriteAuthor;

    @Column(name = "reading_level")
    private String readingLevel;

    @Column(name = "receive_recommendations")
    private boolean receiveRecommendations;

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public String getFavoriteAuthor() {
        return favoriteAuthor;
    }

    public void setFavoriteAuthor(String favoriteAuthor) {
        this.favoriteAuthor = favoriteAuthor;
    }

    public String getReadingLevel() {
        return readingLevel;
    }

    public void setReadingLevel(String readingLevel) {
        this.readingLevel = readingLevel;
    }

    public boolean isReceiveRecommendations() {
        return receiveRecommendations;
    }

    public void setReceiveRecommendations(boolean receiveRecommendations) {
        this.receiveRecommendations = receiveRecommendations;
    }
}
