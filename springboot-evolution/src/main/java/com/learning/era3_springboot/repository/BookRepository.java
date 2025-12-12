package com.learning.era3_springboot.repository;

import com.learning.era3_springboot.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * ERA 3: SPRING BOOT - REPOSITORY (Data Access Layer)
 * ============================================================================
 * 
 * THIS IS THE MAGIC OF SPRING DATA JPA!
 * 
 * By simply extending JpaRepository, you get FOR FREE:
 * - save(entity)        → INSERT or UPDATE
 * - findById(id)        → SELECT by primary key
 * - findAll()           → SELECT all
 * - deleteById(id)      → DELETE by primary key
 * - delete(entity)      → DELETE
 * - count()             → COUNT
 * - existsById(id)      → EXISTS check
 * - And many more...
 * 
 * NO IMPLEMENTATION NEEDED! Spring creates the implementation at runtime!
 * 
 * Compare to Servlet era:
 * - 50+ lines of JDBC code for each operation
 * - Manual connection management
 * - Manual ResultSet to Object mapping
 * - Manual SQL writing
 * 
 * Compare to Spring Framework (without Spring Data):
 * - Still needed to write DAO implementation classes
 * - JdbcTemplate or HibernateTemplate usage
 * - Manual query writing
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // ========================================================================
    // DERIVED QUERY METHODS
    // ========================================================================
    // Spring Data creates queries just from method names!
    // No SQL writing needed!
    
    /**
     * Find books by author name
     * Spring automatically generates: SELECT * FROM books WHERE author = ?
     */
    List<Book> findByAuthor(String author);
    
    /**
     * Find books by title containing a keyword (case-insensitive)
     * Generates: SELECT * FROM books WHERE LOWER(title) LIKE LOWER('%keyword%')
     */
    List<Book> findByTitleContainingIgnoreCase(String keyword);
    
    /**
     * Find books by author containing a keyword
     */
    List<Book> findByAuthorContainingIgnoreCase(String keyword);
    
    /**
     * Find books cheaper than a given price
     * Generates: SELECT * FROM books WHERE price < ?
     */
    List<Book> findByPriceLessThan(Double price);
    
    /**
     * Find books within a price range
     * Generates: SELECT * FROM books WHERE price BETWEEN ? AND ?
     */
    List<Book> findByPriceBetween(Double minPrice, Double maxPrice);
    
    /**
     * Find books by publication year
     */
    List<Book> findByPublicationYear(Integer year);
    
    /**
     * Find books published after a given year
     */
    List<Book> findByPublicationYearGreaterThan(Integer year);
    
    /**
     * Find book by ISBN
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Check if a book with given ISBN exists
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * Find books by author, ordered by price descending
     */
    List<Book> findByAuthorOrderByPriceDesc(String author);
    
    /**
     * Find top 5 most expensive books
     */
    List<Book> findTop5ByOrderByPriceDesc();
    
    /**
     * Find books by title and author
     */
    List<Book> findByTitleAndAuthor(String title, String author);
    
    /**
     * Find books by title or author containing keyword
     */
    List<Book> findByTitleContainingOrAuthorContaining(String titleKeyword, String authorKeyword);
    
    // ========================================================================
    // CUSTOM QUERIES (When method naming isn't enough)
    // ========================================================================
    
    /**
     * Custom JPQL query - Search books by title or author
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);
    
    /**
     * Custom query - Get average book price
     */
    @Query("SELECT AVG(b.price) FROM Book b")
    Double getAveragePrice();
    
    /**
     * Custom query - Get books count by author
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author = :author")
    Long countByAuthor(@Param("author") String author);
    
    /**
     * Native SQL query (when JPQL isn't sufficient)
     */
    @Query(value = "SELECT * FROM books WHERE price > (SELECT AVG(price) FROM books)", 
           nativeQuery = true)
    List<Book> findBooksAboveAveragePrice();
}

/*
 * ============================================================================
 * HOW SPRING DATA JPA CREATES IMPLEMENTATIONS
 * ============================================================================
 * 
 * Method Naming Convention:
 * 
 * findBy  + PropertyName + Condition
 * 
 * Conditions:
 * - Is, Equals           → WHERE x = ?
 * - Not                  → WHERE x != ?
 * - IsNull               → WHERE x IS NULL
 * - IsNotNull            → WHERE x IS NOT NULL
 * - LessThan             → WHERE x < ?
 * - LessThanEqual        → WHERE x <= ?
 * - GreaterThan          → WHERE x > ?
 * - GreaterThanEqual     → WHERE x >= ?
 * - Between              → WHERE x BETWEEN ? AND ?
 * - Like                 → WHERE x LIKE ?
 * - Containing           → WHERE x LIKE %?%
 * - StartingWith         → WHERE x LIKE ?%
 * - EndingWith           → WHERE x LIKE %?
 * - In                   → WHERE x IN (?)
 * - OrderBy              → ORDER BY x
 * - True, False          → WHERE x = true/false
 * 
 * Combine with And/Or:
 * - findByTitleAndAuthor
 * - findByTitleOrAuthor
 * 
 * This eliminates 90% of database query writing!
 */

