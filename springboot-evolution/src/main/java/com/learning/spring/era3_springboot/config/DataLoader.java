package com.learning.spring.era3_springboot.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.learning.spring.era3_springboot.entity.Book;
import com.learning.spring.era3_springboot.repository.BookRepository;

/**
 * ============================================================================
 * DATA LOADER - Load Sample Data on Application Startup
 * ============================================================================
 * 
 * CommandLineRunner runs after the application context is loaded.
 * Perfect for:
 * - Loading sample/test data
 * - Running migrations
 * - Performing health checks
 * - Any initialization that needs Spring beans
 * 
 * COMPARE TO SERVLET ERA:
 * - Would need a ServletContextListener
 * - Manual Spring context access
 * - Complex lifecycle management
 */
@Component
public class DataLoader implements CommandLineRunner {
    
    private final BookRepository bookRepository;
    
    public DataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Override
    public void run(String... args) {
        // Only load data if database is empty
        if (bookRepository.count() == 0) {
            loadSampleBooks();
        }
    }
    
    private void loadSampleBooks() {
        System.out.println("📚 Loading sample books into database...");
        
        // Classic Programming Books
        bookRepository.save(new Book(
                "Clean Code", 
                "Robert C. Martin", 
                45.99,
                "A Handbook of Agile Software Craftsmanship. Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.",
                "9780132350884",
                2008
        ));
        
        bookRepository.save(new Book(
                "Design Patterns", 
                "Gang of Four", 
                59.99,
                "Elements of Reusable Object-Oriented Software. Capturing a wealth of experience about the design of object-oriented software.",
                "9780201633610",
                1994
        ));
        
        bookRepository.save(new Book(
                "The Pragmatic Programmer", 
                "David Thomas & Andrew Hunt", 
                49.99,
                "Your Journey to Mastery. One of the most significant books in the field of software development.",
                "9780135957059",
                2019
        ));
        
        bookRepository.save(new Book(
                "Effective Java", 
                "Joshua Bloch", 
                54.99,
                "Best Practices for the Java Platform. The definitive guide to Java programming language best practices.",
                "9780134685991",
                2018
        ));
        
        bookRepository.save(new Book(
                "Head First Design Patterns", 
                "Eric Freeman & Elisabeth Robson", 
                44.99,
                "Building Extensible and Maintainable Object-Oriented Software. A brain-friendly guide to design patterns.",
                "9781492078005",
                2020
        ));
        
        // Spring Books
        bookRepository.save(new Book(
                "Spring in Action", 
                "Craig Walls", 
                49.99,
                "Covers Spring 5, Spring Boot 2, and Spring Cloud. The most comprehensive guide to Spring development.",
                "9781617294945",
                2018
        ));
        
        bookRepository.save(new Book(
                "Spring Boot in Action", 
                "Craig Walls", 
                44.99,
                "A developer-focused guide to building applications with Spring Boot.",
                "9781617292545",
                2015
        ));
        
        // More classics
        bookRepository.save(new Book(
                "Refactoring", 
                "Martin Fowler", 
                52.99,
                "Improving the Design of Existing Code. The definitive guide to refactoring.",
                "9780134757599",
                2018
        ));
        
        bookRepository.save(new Book(
                "Domain-Driven Design", 
                "Eric Evans", 
                62.99,
                "Tackling Complexity in the Heart of Software. The foundational book on DDD.",
                "9780321125217",
                2003
        ));
        
        bookRepository.save(new Book(
                "Test Driven Development", 
                "Kent Beck", 
                42.99,
                "By Example. The classic guide to TDD from its creator.",
                "9780321146533",
                2002
        ));
        
        System.out.println("✅ Loaded " + bookRepository.count() + " sample books!");
    }
}

