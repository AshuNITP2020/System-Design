package com.learning.era2_spring;

/**
 * ============================================================================
 * WHY IS IT CALLED SPRING "MVC"?
 * ============================================================================
 * 
 * MVC = Model - View - Controller
 * 
 * It's a SOFTWARE DESIGN PATTERN that separates an application into 3 parts:
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                        THE MVC PATTERN                                  │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │                          ┌─────────────┐                                │
 * │                          │   USER      │                                │
 * │                          │  (Browser)  │                                │
 * │                          └──────┬──────┘                                │
 * │                                 │                                       │
 * │                          sees   │   interacts                           │
 * │                                 ▼                                       │
 * │                          ┌─────────────┐                                │
 * │                          │    VIEW     │                                │
 * │                          │             │                                │
 * │                          │ (What user  │                                │
 * │                          │   sees)     │                                │
 * │                          └──────┬──────┘                                │
 * │                                 │                                       │
 * │              ┌──────────────────┼──────────────────┐                    │
 * │              │                  │                  │                    │
 * │              ▼                  │                  ▼                    │
 * │       ┌─────────────┐           │           ┌─────────────┐             │
 * │       │ CONTROLLER  │◄──────────┘           │   MODEL     │             │
 * │       │             │                       │             │             │
 * │       │ (Handles    │ ─────────────────────►│ (Data &     │             │
 * │       │  requests)  │    updates/fetches    │  Business   │             │
 * │       │             │◄───────────────────── │  Logic)     │             │
 * │       └─────────────┘                       └─────────────┘             │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * ============================================================================
 */

// ============================================================================
// THE THREE COMPONENTS EXPLAINED
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  1. MODEL - The Data & Business Logic                                  │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  • Contains your DATA (entities, DTOs)                                 │
 * │  • Contains your BUSINESS LOGIC (services)                             │
 * │  • Contains your DATA ACCESS (repositories)                            │
 * │                                                                         │
 * │  In Spring:                                                             │
 * │  - Entity classes (Book, User, Order)                                  │
 * │  - @Service classes (BookService, UserService)                         │
 * │  - @Repository classes (BookRepository)                                │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// MODEL Example
class Book {  // Entity - represents data
    private Long id;
    private String title;
    private String author;
    // This is part of MODEL
}

// @Service  
class BookService {  // Business Logic - also part of MODEL
    // Business rules like "can't delete a book with active rentals"
    public void deleteBook(Long id) {
        // Business logic here
    }
}

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  2. VIEW - What the User Sees                                          │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  • The PRESENTATION layer                                              │
 * │  • What gets displayed to the user                                     │
 * │                                                                         │
 * │  Traditional Web (JSP/Thymeleaf era):                                  │
 * │  - JSP pages (.jsp files)                                              │
 * │  - Thymeleaf templates (.html files)                                   │
 * │  - FreeMarker templates                                                │
 * │                                                                         │
 * │  Modern REST APIs (what we use now):                                   │
 * │  - JSON response (the "view" is JSON data)                             │
 * │  - Frontend (React, Angular, Vue) renders the actual view              │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

/*
 * Traditional View (JSP) - What Spring MVC was originally designed for:
 * 
 * /WEB-INF/views/books.jsp:
 * 
 * <%@ page contentType="text/html;charset=UTF-8" %>
 * <html>
 * <head><title>Books</title></head>
 * <body>
 *     <h1>Book List</h1>
 *     <table>
 *         <c:forEach items="${books}" var="book">
 *             <tr>
 *                 <td>${book.title}</td>
 *                 <td>${book.author}</td>
 *             </tr>
 *         </c:forEach>
 *     </table>
 * </body>
 * </html>
 * 
 * 
 * Modern View (JSON) - What we mostly use today:
 * 
 * {
 *     "books": [
 *         {"id": 1, "title": "Clean Code", "author": "Robert Martin"},
 *         {"id": 2, "title": "Design Patterns", "author": "Gang of Four"}
 *     ]
 * }
 * 
 * The actual VIEW is now rendered by React/Angular/Vue on the frontend!
 */

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  3. CONTROLLER - The Traffic Cop                                       │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  • Receives HTTP requests from user                                    │
 * │  • Decides what to do with the request                                 │
 * │  • Calls the appropriate MODEL (service/repository)                    │
 * │  • Returns the appropriate VIEW (JSP or JSON)                          │
 * │                                                                         │
 * │  In Spring:                                                             │
 * │  - @Controller classes (returns View names)                            │
 * │  - @RestController classes (returns data directly as JSON)             │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// CONTROLLER Example

// Traditional MVC Controller (returns VIEW name)
// @Controller
class BookController {
    private BookService bookService;
    
    // @GetMapping("/books")
    public String showBooks(Object model) {  // Model parameter here!
        // model.addAttribute("books", bookService.getAllBooks());
        return "books";  // Returns VIEW NAME → /WEB-INF/views/books.jsp
    }
}

// REST Controller (returns DATA, not view name)
// @RestController
class BookRestController {
    private BookService bookService;
    
    // @GetMapping("/api/books")
    public Object getBooks() {
        return null; // bookService.getAllBooks();  // Returns DATA → converted to JSON
    }
}

// ============================================================================
// HOW SPRING MVC WORKS - THE COMPLETE FLOW
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                    SPRING MVC REQUEST FLOW                              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  User types: http://localhost:8080/books                               │
 * │                                                                         │
 * │     ┌──────────┐                                                        │
 * │     │ Browser  │                                                        │
 * │     └────┬─────┘                                                        │
 * │          │ HTTP Request: GET /books                                     │
 * │          ▼                                                              │
 * │  ┌───────────────────┐                                                  │
 * │  │ DispatcherServlet │  ← FRONT CONTROLLER (receives ALL requests)     │
 * │  │                   │                                                  │
 * │  │  "I received a    │                                                  │
 * │  │   request for     │                                                  │
 * │  │   /books"         │                                                  │
 * │  └─────────┬─────────┘                                                  │
 * │            │                                                            │
 * │            │ "Who handles /books?"                                      │
 * │            ▼                                                            │
 * │  ┌───────────────────┐                                                  │
 * │  │  HandlerMapping   │  ← Finds the right @Controller                  │
 * │  │                   │                                                  │
 * │  │  "BookController  │                                                  │
 * │  │   handles /books" │                                                  │
 * │  └─────────┬─────────┘                                                  │
 * │            │                                                            │
 * │            ▼                                                            │
 * │  ┌───────────────────┐                                                  │
 * │  │  BookController   │  ← CONTROLLER                                   │
 * │  │                   │                                                  │
 * │  │  @GetMapping      │                                                  │
 * │  │  showBooks() {    │                                                  │
 * │  │    // call model  │──────┐                                          │
 * │  │  }                │      │                                          │
 * │  └───────────────────┘      │                                          │
 * │            │                │                                          │
 * │            │                ▼                                          │
 * │            │      ┌───────────────────┐                                 │
 * │            │      │   BookService     │  ← MODEL (Business Logic)      │
 * │            │      │                   │                                 │
 * │            │      │  getAllBooks() {  │                                 │
 * │            │      │    // fetch data  │                                 │
 * │            │      │  }                │                                 │
 * │            │      └─────────┬─────────┘                                 │
 * │            │                │                                          │
 * │            │                ▼                                          │
 * │            │      ┌───────────────────┐                                 │
 * │            │      │  BookRepository   │  ← MODEL (Data Access)         │
 * │            │      │                   │                                 │
 * │            │      │  findAll() →      │                                 │
 * │            │      │  [Book1, Book2]   │                                 │
 * │            │      └─────────┬─────────┘                                 │
 * │            │                │                                          │
 * │            │◄───────────────┘                                          │
 * │            │  returns List<Book>                                       │
 * │            │                                                            │
 * │            │  Controller returns "books" (view name)                   │
 * │            ▼                                                            │
 * │  ┌───────────────────┐                                                  │
 * │  │   ViewResolver    │  ← Finds the VIEW template                      │
 * │  │                   │                                                  │
 * │  │  "books" →        │                                                  │
 * │  │  /WEB-INF/views/  │                                                  │
 * │  │  books.jsp        │                                                  │
 * │  └─────────┬─────────┘                                                  │
 * │            │                                                            │
 * │            ▼                                                            │
 * │  ┌───────────────────┐                                                  │
 * │  │    books.jsp      │  ← VIEW (renders HTML)                          │
 * │  │                   │                                                  │
 * │  │  <html>           │                                                  │
 * │  │    <h1>Books</h1> │                                                  │
 * │  │    <table>...</>  │                                                  │
 * │  │  </html>          │                                                  │
 * │  └─────────┬─────────┘                                                  │
 * │            │                                                            │
 * │            │  HTML Response                                            │
 * │            ▼                                                            │
 * │     ┌──────────┐                                                        │
 * │     │ Browser  │  ← User sees the rendered page!                       │
 * │     └──────────┘                                                        │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// @Controller vs @RestController - TWO TYPES OF CONTROLLERS
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  @Controller (Traditional MVC - returns VIEW)                          │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  Used when you want to return HTML pages (JSP, Thymeleaf)              │
 * │                                                                         │
 * │  @Controller                                                            │
 * │  public class BookController {                                         │
 * │                                                                         │
 * │      @GetMapping("/books")                                             │
 * │      public String showBooks(Model model) {                            │
 * │          List<Book> books = bookService.getAllBooks();                 │
 * │          model.addAttribute("books", books);  // Add data to model     │
 * │          return "books";  // ← This is a VIEW NAME, not data!         │
 * │      }                        // ViewResolver finds books.jsp          │
 * │  }                                                                      │
 * │                                                                         │
 * │  Flow: Request → Controller → Model → View (JSP) → HTML Response       │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  @RestController (REST API - returns DATA)                             │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  Used when you want to return JSON/XML data (for APIs)                 │
 * │  @RestController = @Controller + @ResponseBody                         │
 * │                                                                         │
 * │  @RestController                                                        │
 * │  @RequestMapping("/api")                                               │
 * │  public class BookRestController {                                     │
 * │                                                                         │
 * │      @GetMapping("/books")                                             │
 * │      public List<Book> getBooks() {                                    │
 * │          return bookService.getAllBooks();  // ← Returns DATA directly │
 * │      }                                       // Jackson converts to JSON│
 * │  }                                                                      │
 * │                                                                         │
 * │  Flow: Request → Controller → Model → JSON Response (no View!)         │
 * │                                                                         │
 * │  Response: {"books": [{"id": 1, "title": "Clean Code"}, ...]}         │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// WHY "MVC" EVEN FOR REST APIs?
// ============================================================================

/*
 * You might think: "If I'm building REST APIs, there's no VIEW (JSP/HTML),
 * so why is it still called MVC?"
 * 
 * ANSWER: The JSON response IS the "view"!
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                                                                         │
 * │   Traditional MVC:              REST API "MVC":                        │
 * │                                                                         │
 * │   Model = Entity + Service      Model = Entity + Service + Repository  │
 * │   View = JSP/Thymeleaf          View = JSON (HttpMessageConverter)     │
 * │   Controller = @Controller      Controller = @RestController           │
 * │                                                                         │
 * │   Output: HTML                  Output: JSON                           │
 * │   Consumer: Browser             Consumer: Frontend App / Mobile App    │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * Spring MVC handles BOTH cases - that's why it's so powerful!
 */

// ============================================================================
// WHERE DOES DispatcherServlet FIT?
// ============================================================================

/*
 * DispatcherServlet is the FRONT CONTROLLER in Spring MVC.
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                                                                         │
 * │  Without DispatcherServlet (Servlet Era):                              │
 * │                                                                         │
 * │  /books     →  BookServlet                                             │
 * │  /users     →  UserServlet                                             │
 * │  /orders    →  OrderServlet                                            │
 * │  /products  →  ProductServlet                                          │
 * │                                                                         │
 * │  Each URL had its OWN servlet! (configured in web.xml)                 │
 * │                                                                         │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  With DispatcherServlet (Spring MVC):                                  │
 * │                                                                         │
 * │  ALL REQUESTS  →  DispatcherServlet  →  Routes to correct Controller   │
 * │                                                                         │
 * │  /books     ──┐                      ┌──→ BookController               │
 * │  /users     ──┼──→ DispatcherServlet ├──→ UserController               │
 * │  /orders    ──┤         │            ├──→ OrderController              │
 * │  /products  ──┘         │            └──→ ProductController            │
 * │                         │                                              │
 * │                  (Front Controller)                                    │
 * │                         │                                              │
 * │              ┌──────────┴──────────┐                                   │
 * │              │ Uses HandlerMapping │                                   │
 * │              │ to find the right   │                                   │
 * │              │ @Controller method  │                                   │
 * │              └─────────────────────┘                                   │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * Benefits of Front Controller Pattern:
 * - One entry point for all requests
 * - Centralized handling (security, logging, etc.)
 * - Clean URL routing via annotations
 */

// ============================================================================
// SUMMARY: MVC COMPONENTS IN SPRING
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  COMPONENT    │  SPRING ANNOTATION    │  PURPOSE                       │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  Model        │  @Entity              │  Data structure                │
 * │               │  @Service             │  Business logic                │
 * │               │  @Repository          │  Data access                   │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  View         │  JSP/Thymeleaf        │  HTML templates (traditional)  │
 * │               │  JSON response        │  Data for frontend (REST API)  │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  Controller   │  @Controller          │  Returns view name             │
 * │               │  @RestController      │  Returns data (JSON)           │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * 
 * Spring MVC is called "MVC" because it implements the Model-View-Controller
 * pattern to separate concerns:
 * 
 * - Model: Your data and business logic (independent of web layer)
 * - View: How data is presented (HTML, JSON, XML)
 * - Controller: Handles HTTP requests and coordinates M and V
 * 
 * This separation makes code:
 * ✅ Easier to test (test service without controller)
 * ✅ Easier to maintain (change view without changing logic)
 * ✅ Easier to scale (different teams work on different layers)
 */

