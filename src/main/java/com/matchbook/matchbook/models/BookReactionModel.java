package com.matchbook.matchbook.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// What a user did with a book in the stack. One reaction per user and book, enforced by the database.
@Entity
@Table(name = "book_reactions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"firebase_uid", "book_id"}))
public class BookReactionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "firebase_uid", length = 128, nullable = false)
    private String firebaseUid;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReactionType type;

    @Column(name = "reacted_at", nullable = false)
    private LocalDateTime reactedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }

    public LocalDateTime getReactedAt() {
        return reactedAt;
    }

    public void setReactedAt(LocalDateTime reactedAt) {
        this.reactedAt = reactedAt;
    }
}
