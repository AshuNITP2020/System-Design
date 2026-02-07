package com.learning.spring.era3_springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learning.spring.era3_springboot.dto.BookDTO;
import com.learning.spring.era3_springboot.entity.Book;
import com.learning.spring.era3_springboot.repository.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * ERA 3: SPRING BOOT - SERVICE (Business Logic Layer)
 * ============================================================================
 * 
 * The Service layer contains business logic and orchestrates operations.
 * 
 * KEY ANNOTATIONS:
 * @Service - Marks this as a Spring-managed service bean
 * @Transactional - Provides declarative transaction management
 * @Autowired - Injects dependencies automatically
 * 
 * BENEFITS:
 * 1. Clear separation of concerns
 * 2. Business logic in one place
 * 3. Easy to unit test (mock the repository)
 * 4. Transaction management without boilerplate
 * 5. Can combine multiple repository operations
 */
@Service
@Transactional  // All methods are transactional by default
public class BookService {
    
    private final BookRepository bookRepository;
    
    /**
     * CONSTRUCTOR INJECTION (Recommended over field injection)
     * 
     * Benefits:
     * - Dependencies are explicit
     * - Easy to test (just pass mocks in constructor)
     * - Fields can be final (immutable)
     * - Fails fast if dependency is missing
     */
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    // ========================================================================
    // CRUD OPERATIONS
    // ========================================================================
    
    /**
     * Get all books
     */
    @Transactional(readOnly = true)  // Optimization for read-only operations
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get book by ID
     */
    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    /**
     * Create a new book
     */
    public BookDTO createBook(BookDTO bookDTO) {
        // Validate ISBN uniqueness
        if (bookDTO.getIsbn() != null && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }
        
        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }
    
    /**
     * Update an existing book
     */
    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    // Check if ISBN is being changed to an existing one
                    if (bookDTO.getIsbn() != null && 
                        !bookDTO.getIsbn().equals(existingBook.getIsbn()) &&
                        bookRepository.existsByIsbn(bookDTO.getIsbn())) {
                        throw new IllegalArgumentException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
                    }
                    
                    // Update fields
                    existingBook.setTitle(bookDTO.getTitle());
                    existingBook.setAuthor(bookDTO.getAuthor());
                    existingBook.setPrice(bookDTO.getPrice());
                    existingBook.setDescription(bookDTO.getDescription());
                    existingBook.setIsbn(bookDTO.getIsbn());
                    existingBook.setPublicationYear(bookDTO.getPublicationYear());
                    
                    // JPA automatically persists changes (dirty checking)
                    Book updatedBook = bookRepository.save(existingBook);
                    return convertToDTO(updatedBook);
                });
    }
    
    /**
     * Delete a book
     */
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // ========================================================================
    // SEARCH OPERATIONS
    // ========================================================================
    
    /**
     * Search books by keyword (title or author)
     */
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get books by author
     */
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get books in price range
     */
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksInPriceRange(Double minPrice, Double maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get top expensive books
     */
    @Transactional(readOnly = true)
    public List<BookDTO> getTopExpensiveBooks() {
        return bookRepository.findTop5ByOrderByPriceDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get book by ISBN
     */
    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::convertToDTO);
    }
    
    /**
     * Get average book price
     */
    @Transactional(readOnly = true)
    public Double getAverageBookPrice() {
        return bookRepository.getAveragePrice();
    }
    
    // ========================================================================
    // DTO <-> ENTITY CONVERSION
    // ========================================================================
    // In production, use MapStruct or ModelMapper for this
    
    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getDescription(),
                book.getIsbn(),
                book.getPublicationYear()
        );
    }
    
    private Book convertToEntity(BookDTO dto) {
        return new Book(
                dto.getTitle(),
                dto.getAuthor(),
                dto.getPrice(),
                dto.getDescription(),
                dto.getIsbn(),
                dto.getPublicationYear()
        );
    }
}

/*
 * ============================================================================
 * TRANSACTION MANAGEMENT EXPLAINED
 * ============================================================================
 * 
 * @Transactional does all this automatically:
 * 
 * public void createBook(BookDTO dto) {
 *     Transaction tx = null;
 *     try {
 *         tx = entityManager.getTransaction();
 *         tx.begin();                    // Spring does this
 *         
 *         // Your code here
 *         bookRepository.save(book);
 *         
 *         tx.commit();                   // Spring does this
 *     } catch (Exception e) {
 *         if (tx != null) tx.rollback(); // Spring does this
 *         throw e;
 *     }
 * }
 * 
 * @Transactional options:
 * - readOnly = true    → Optimization hint for read operations
 * - timeout = 30       → Transaction timeout in seconds
 * - rollbackFor = {Exception.class}  → Which exceptions trigger rollback
 * - propagation = REQUIRED/REQUIRES_NEW/etc → Transaction propagation
 * - isolation = READ_COMMITTED/etc → Transaction isolation level
 */

