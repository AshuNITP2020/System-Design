package com.learning.spring.era1_servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ERA 1: SERVLET-BASED DEVELOPMENT (1997-2003)
 * ============================================================================
 * 
 * This is how web applications were built before Spring!
 * 
 * PAIN POINTS DEMONSTRATED HERE:
 * 1. Manual HTTP request/response handling
 * 2. Manual JSON parsing and creation
 * 3. Manual database connection management
 * 4. No separation of concerns - everything mixed in one class
 * 5. No dependency injection - tightly coupled code
 * 6. Manual resource cleanup
 * 7. Difficult to test
 * 
 * ADDITIONAL REQUIREMENTS (not shown in code):
 * - web.xml configuration for every servlet
 * - External Tomcat server installation and configuration
 * - WAR file deployment
 * - Manual session management
 * 
 * NOTE: This is a simulation. In real servlet apps, you'd also need:
 * - A web.xml file to register this servlet
 * - An external Tomcat/Jetty server
 * - Manual build and deploy process
 */
public class BookServlet extends HttpServlet {
    
    // Database configuration - HARDCODED! No externalization!
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    // ========================================================================
    // PROBLEM 1: Manual HTTP method handling
    // Every HTTP method needs its own implementation
    // ========================================================================
    
    /**
     * Handle GET requests - List all books or get one book by ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // PROBLEM: Manual content type setting
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // PROBLEM: Manual URL parsing to determine what action to take
            String pathInfo = request.getPathInfo(); // e.g., /123 for /books/123
            
            // PROBLEM: Manual database connection (no connection pooling)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            if (pathInfo != null && pathInfo.length() > 1) {
                // Get single book by ID
                String bookId = pathInfo.substring(1);
                getBookById(conn, bookId, out, response);
            } else {
                // Get all books
                getAllBooks(conn, out);
            }
            
        } catch (ClassNotFoundException e) {
            // PROBLEM: Manual error handling and response formatting
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Database driver not found: " + e.getMessage() + "\"}");
            
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            
        } finally {
            // PROBLEM: Manual resource cleanup - easy to forget and cause leaks!
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }
    
    /**
     * Handle POST requests - Create a new book
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // PROBLEM: Manual request parameter reading
            // In modern apps, we'd have automatic JSON deserialization
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String priceStr = request.getParameter("price");
            
            // PROBLEM: Manual validation - no declarative validation
            List<String> errors = new ArrayList<>();
            
            if (title == null || title.trim().isEmpty()) {
                errors.add("Title is required");
            }
            
            if (author == null || author.trim().isEmpty()) {
                errors.add("Author is required");
            }
            
            double price = 0;
            if (priceStr == null || priceStr.trim().isEmpty()) {
                errors.add("Price is required");
            } else {
                try {
                    price = Double.parseDouble(priceStr);
                    if (price <= 0) {
                        errors.add("Price must be positive");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Price must be a valid number");
                }
            }
            
            // Return validation errors
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"errors\": " + toJsonArray(errors) + "}");
                return;
            }
            
            // Database operation
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            stmt = conn.prepareStatement(
                "INSERT INTO books (title, author, price) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setDouble(3, price);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }
            
            // Get generated ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            long id = 0;
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }
            
            // PROBLEM: Manual JSON response creation
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.println(String.format(
                "{\"id\": %d, \"title\": \"%s\", \"author\": \"%s\", \"price\": %.2f}",
                id, escapeJson(title), escapeJson(author), price
            ));
            
        } catch (ClassNotFoundException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }
    
    /**
     * Handle PUT requests - Update a book
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Book ID is required in path\"}");
            return;
        }
        
        // Similar verbose code for update...
        // PROBLEM: Massive code duplication with doPost!
        out.println("{\"message\": \"PUT not fully implemented - see the pattern\"}");
    }
    
    /**
     * Handle DELETE requests - Delete a book
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Book ID is required\"}");
                return;
            }
            
            String bookId = pathInfo.substring(1);
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            stmt = conn.prepareStatement("DELETE FROM books WHERE id = ?");
            stmt.setString(1, bookId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"error\": \"Book not found\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                out.println("{\"message\": \"Book deleted successfully\"}");
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }
    
    // ========================================================================
    // HELPER METHODS - In servlet era, you wrote all these yourself!
    // ========================================================================
    
    private void getAllBooks(Connection conn, PrintWriter out) throws SQLException {
        StringBuilder json = new StringBuilder("[");
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;
                
                // PROBLEM: Manual JSON construction - error-prone!
                json.append(String.format(
                    "{\"id\": %d, \"title\": \"%s\", \"author\": \"%s\", \"price\": %.2f}",
                    rs.getLong("id"),
                    escapeJson(rs.getString("title")),
                    escapeJson(rs.getString("author")),
                    rs.getDouble("price")
                ));
            }
        }
        
        json.append("]");
        out.println(json.toString());
    }
    
    private void getBookById(Connection conn, String bookId, PrintWriter out, 
                             HttpServletResponse response) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            stmt.setString(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    out.println(String.format(
                        "{\"id\": %d, \"title\": \"%s\", \"author\": \"%s\", \"price\": %.2f}",
                        rs.getLong("id"),
                        escapeJson(rs.getString("title")),
                        escapeJson(rs.getString("author")),
                        rs.getDouble("price")
                    ));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Book not found\"}");
                }
            }
        }
    }
    
    // PROBLEM: Manual JSON escaping - easy to get wrong and cause security issues!
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private String toJsonArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
    
    // Resource cleanup helpers
    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                // Swallow exception during cleanup
            }
        }
    }
}

/**
 * ============================================================================
 * WHAT YOU WOULD ALSO NEED (web.xml) - In a separate file!
 * ============================================================================
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="4.0">
 *     <servlet>
 *         <servlet-name>BookServlet</servlet-name>
 *         <servlet-class>com.learning.era1_servlets.BookServlet</servlet-class>
 *     </servlet>
 *     <servlet-mapping>
 *         <servlet-name>BookServlet</servlet-name>
 *         <url-pattern>/books/*</url-pattern>
 *     </servlet-mapping>
 * </web-app>
 * 
 * ============================================================================
 * DEPLOYMENT STEPS:
 * ============================================================================
 * 1. Compile all Java files
 * 2. Create directory structure: WEB-INF/classes/
 * 3. Copy compiled classes to WEB-INF/classes/
 * 4. Create web.xml in WEB-INF/
 * 5. Package as WAR file
 * 6. Copy WAR to Tomcat's webapps/ directory
 * 7. Start Tomcat
 * 8. Wait for deployment
 * 9. Access at http://localhost:8080/your-app/books
 * 
 * IMAGINE DOING THIS FOR EVERY CODE CHANGE!
 */

