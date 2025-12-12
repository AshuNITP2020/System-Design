package com.learning.era3_springboot.dto;

import jakarta.validation.constraints.*;

/**
 * ============================================================================
 * ERA 3: SPRING BOOT - DATA TRANSFER OBJECT (DTO)
 * ============================================================================
 * 
 * DTOs are used to:
 * 1. Separate API contract from database entity
 * 2. Control what data is exposed to clients
 * 3. Add request-specific validation
 * 4. Transform/combine data from multiple entities
 * 
 * WHY NOT USE ENTITY DIRECTLY?
 * - Entity might have sensitive fields (password, internal IDs)
 * - Entity might have lazy-loaded relationships causing N+1 queries
 * - API contract should be independent of database schema
 * - Versioning: API v1 might need different fields than v2
 */
public class BookDTO {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    private String isbn;
    
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2100, message = "Publication year cannot exceed 2100")
    private Integer publicationYear;
    
    // ========================================================================
    // CONSTRUCTORS
    // ========================================================================
    
    public BookDTO() {
    }
    
    public BookDTO(Long id, String title, String author, Double price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
    }
    
    public BookDTO(Long id, String title, String author, Double price, 
                   String description, String isbn, Integer publicationYear) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }
    
    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public Integer getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
}

