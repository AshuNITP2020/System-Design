package com.learning.spring.era3_springboot.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learning.spring.era3_springboot.dto.ApiResponse;
import com.learning.spring.era3_springboot.dto.BookDTO;
import com.learning.spring.era3_springboot.service.BookService;

import java.util.List;

/**
 * ============================================================================
 * ERA 3: SPRING BOOT - REST CONTROLLER
 * ============================================================================
 * 
 * The Controller handles HTTP requests and responses.
 * 
 * KEY ANNOTATIONS:
 * @RestController = @Controller + @ResponseBody
 *   - Marks class as REST API controller
 *   - Returns JSON/XML directly (not view names)
 * 
 * @RequestMapping - Base path for all endpoints in this controller
 * @GetMapping, @PostMapping, etc. - HTTP method mappings
 * @PathVariable - Extract values from URL path
 * @RequestParam - Extract query parameters
 * @RequestBody - Parse JSON request body to object
 * @Valid - Trigger validation on request body
 * 
 * COMPARE TO SERVLET ERA:
 * - No manual JSON parsing/generation
 * - No manual HTTP status setting (mostly)
 * - No manual content-type handling
 * - Declarative URL mapping
 * - Automatic request body deserialization
 * - Automatic response body serialization
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    // ========================================================================
    // CRUD ENDPOINTS
    // ========================================================================
    
    /**
     * GET /api/books - Get all books
     * 
     * Example: curl http://localhost:8080/api/books
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("Retrieved " + books.size() + " books", books));
    }
    
    /**
     * GET /api/books/{id} - Get book by ID
     * 
     * @PathVariable extracts 'id' from the URL path
     * Example: curl http://localhost:8080/api/books/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(ApiResponse.success(book)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Book not found with id: " + id)));
    }
    
    /**
     * POST /api/books - Create a new book
     * 
     * @RequestBody parses JSON body to BookDTO
     * @Valid triggers validation annotations on BookDTO
     * 
     * Example:
     * curl -X POST http://localhost:8080/api/books \
     *   -H "Content-Type: application/json" \
     *   -d '{"title":"Clean Code","author":"Robert Martin","price":45.99}'
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        try {
            BookDTO createdBook = bookService.createBook(bookDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Book created successfully", createdBook));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * PUT /api/books/{id} - Update an existing book
     * 
     * Example:
     * curl -X PUT http://localhost:8080/api/books/1 \
     *   -H "Content-Type: application/json" \
     *   -d '{"title":"Clean Code 2nd Ed","author":"Robert Martin","price":49.99}'
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(
            @PathVariable Long id, 
            @Valid @RequestBody BookDTO bookDTO) {
        try {
            return bookService.updateBook(id, bookDTO)
                    .map(book -> ResponseEntity.ok(ApiResponse.success("Book updated successfully", book)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Book not found with id: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/books/{id} - Delete a book
     * 
     * Example: curl -X DELETE http://localhost:8080/api/books/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        if (bookService.deleteBook(id)) {
            return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Book not found with id: " + id));
    }
    
    // ========================================================================
    // SEARCH ENDPOINTS
    // ========================================================================
    
    /**
     * GET /api/books/search?q=keyword - Search books
     * 
     * @RequestParam extracts query parameter 'q'
     * Example: curl http://localhost:8080/api/books/search?q=java
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchBooks(
            @RequestParam(name = "q") String keyword) {
        List<BookDTO> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(ApiResponse.success("Found " + books.size() + " books", books));
    }
    
    /**
     * GET /api/books/author/{author} - Get books by author
     * 
     * Example: curl http://localhost:8080/api/books/author/Martin
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByAuthor(
            @PathVariable String author) {
        List<BookDTO> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * GET /api/books/price?min=10&max=50 - Get books in price range
     * 
     * Example: curl "http://localhost:8080/api/books/price?min=10&max=50"
     */
    @GetMapping("/price")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksInPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        List<BookDTO> books = bookService.getBooksInPriceRange(min, max);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * GET /api/books/top-expensive - Get top 5 most expensive books
     * 
     * Example: curl http://localhost:8080/api/books/top-expensive
     */
    @GetMapping("/top-expensive")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getTopExpensiveBooks() {
        List<BookDTO> books = bookService.getTopExpensiveBooks();
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * GET /api/books/isbn/{isbn} - Get book by ISBN
     * 
     * Example: curl http://localhost:8080/api/books/isbn/9780132350884
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn)
                .map(book -> ResponseEntity.ok(ApiResponse.success(book)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Book not found with ISBN: " + isbn)));
    }
    
    /**
     * GET /api/books/stats/average-price - Get average book price
     * 
     * Example: curl http://localhost:8080/api/books/stats/average-price
     */
    @GetMapping("/stats/average-price")
    public ResponseEntity<ApiResponse<Double>> getAveragePrice() {
        Double avgPrice = bookService.getAverageBookPrice();
        return ResponseEntity.ok(ApiResponse.success("Average book price", avgPrice));
    }
}

/*
 * ============================================================================
 * COMPARISON: SERVLET vs SPRING BOOT CONTROLLER
 * ============================================================================
 * 
 * SERVLET ERA (for the same getAllBooks):
 * 
 * protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
 *     resp.setContentType("application/json");
 *     resp.setCharacterEncoding("UTF-8");
 *     Connection conn = null;
 *     try {
 *         Class.forName("com.mysql.jdbc.Driver");
 *         conn = DriverManager.getConnection(URL, USER, PASS);
 *         Statement stmt = conn.createStatement();
 *         ResultSet rs = stmt.executeQuery("SELECT * FROM books");
 *         StringBuilder json = new StringBuilder("[");
 *         // ... 30 more lines of manual JSON building
 *         PrintWriter out = resp.getWriter();
 *         out.println(json.toString());
 *     } catch (Exception e) {
 *         resp.setStatus(500);
 *         resp.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
 *     } finally {
 *         if (conn != null) try { conn.close(); } catch (SQLException e) {}
 *     }
 * }
 * 
 * SPRING BOOT (same functionality):
 * 
 * @GetMapping
 * public List<BookDTO> getAllBooks() {
 *     return bookService.getAllBooks();
 * }
 * 
 * THAT'S IT! 3 lines vs 40+ lines!
 */

