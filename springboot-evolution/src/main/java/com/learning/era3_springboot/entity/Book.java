package com.learning.era3_springboot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * ============================================================================
 * ERA 3: SPRING BOOT - ENTITY
 * ============================================================================
 * 
 * The Entity represents a table in the database.
 * With Spring Boot + JPA, this simple annotation-based class creates:
 * - Database table automatically (if spring.jpa.hibernate.ddl-auto=create/update)
 * - All CRUD operations (via JpaRepository)
 * - Relationship mappings
 * - Validation
 * 
 * Compare this to Servlet era where you had to:
 * - Write CREATE TABLE SQL manually
 * - Write INSERT, UPDATE, DELETE, SELECT queries manually
 * - Map ResultSet to objects manually
 */
@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    @Column(nullable = false)
    private String author;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(length = 2000)
    private String description;
    
    @Size(max = 20, message = "ISBN cannot exceed 20 characters")
    @Column(unique = true)
    private String isbn;
    
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2100, message = "Publication year cannot exceed 2100")
    private Integer publicationYear;
    
    // ========================================================================
    // CONSTRUCTORS
    // ========================================================================
    
    public Book() {
        // Default constructor required by JPA
    }
    
    public Book(String title, String author, Double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }
    
    public Book(String title, String author, Double price, String description, String isbn, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }
    
    // ========================================================================
    // GETTERS AND SETTERS
    // (In production, use Lombok @Data annotation to eliminate this boilerplate!)
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
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}

