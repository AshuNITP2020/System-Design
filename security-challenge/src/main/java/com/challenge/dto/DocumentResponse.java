package com.challenge.dto;

import java.time.LocalDateTime;

public class DocumentResponse {
    
    private Long id;
    private String title;
    private String content;
    private String ownerUsername;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DocumentResponse response = new DocumentResponse();

        public Builder id(Long id) { response.id = id; return this; }
        public Builder title(String title) { response.title = title; return this; }
        public Builder content(String content) { response.content = content; return this; }
        public Builder ownerUsername(String ownerUsername) { response.ownerUsername = ownerUsername; return this; }
        public Builder visibility(String visibility) { response.visibility = visibility; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { response.updatedAt = updatedAt; return this; }

        public DocumentResponse build() { return response; }
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getVisibility() { return visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

