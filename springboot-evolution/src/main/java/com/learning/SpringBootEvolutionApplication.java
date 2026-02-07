package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * SPRING BOOT APPLICATION - THE MAGIC STARTS HERE!
 * ============================================================================
 * 
 * @SpringBootApplication is a meta-annotation that combines:
 * 
 * 1. @Configuration
 *    - Marks this class as a source of bean definitions
 *    - Replaces XML configuration files!
 * 
 * 2. @EnableAutoConfiguration
 *    - Tells Spring Boot to automatically configure beans based on:
 *      • Dependencies on classpath (starter-web → DispatcherServlet)
 *      • Properties in application.properties
 *      • Bean definitions you provide
 *    - This is the MAGIC of Spring Boot!
 * 
 * 3. @ComponentScan
 *    - Scans for @Component, @Service, @Repository, @Controller
 *    - Starting from this package and all sub-packages
 *    - Replaces XML's <context:component-scan>
 * 
 * WHAT HAPPENS WHEN YOU RUN THIS?
 * 
 * 1. SpringApplication.run() starts the application
 * 2. Creates ApplicationContext (Spring container)
 * 3. Auto-configuration kicks in:
 *    - H2 on classpath → Configure DataSource
 *    - JPA on classpath → Configure EntityManager
 *    - Web on classpath → Configure embedded Tomcat
 * 4. Component scanning finds all beans
 * 5. Dependency injection wires everything together
 * 6. Embedded Tomcat starts on port 8080
 * 7. Application is ready!
 * 
 * COMPARE TO PREVIOUS ERAS:
 * 
 * SERVLET ERA:
 * - Install Tomcat separately
 * - Create web.xml with servlet mappings
 * - Build WAR file
 * - Deploy to Tomcat
 * - Start Tomcat
 * 
 * SPRING FRAMEWORK:
 * - Install Tomcat separately
 * - Create web.xml with DispatcherServlet
 * - Create applicationContext.xml
 * - Create spring-mvc.xml
 * - Build WAR file
 * - Deploy to Tomcat
 * - Start Tomcat
 * 
 * SPRING BOOT:
 * - Run this main method
 * - DONE!
 */
@SpringBootApplication
public class SpringBootEvolutionApplication {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        🚀 SPRING BOOT EVOLUTION LEARNING PROJECT 🚀       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  This project demonstrates the evolution from:            ║");
        System.out.println("║    • Raw Servlets (Era 1)                                 ║");
        System.out.println("║    • Spring Framework (Era 2)                             ║");
        System.out.println("║    • Spring Boot (Era 3) ← You are here!                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        // This one line does EVERYTHING:
        // - Creates Spring container
        // - Configures auto-configuration
        // - Starts embedded Tomcat
        // - Scans for components
        // - Wires dependencies
        SpringApplication.run(SpringBootEvolutionApplication.class, args);
        
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("  ✅ Application started successfully!");
        System.out.println("  📖 API Documentation:");
        System.out.println("     GET    /api/books              - Get all books");
        System.out.println("     GET    /api/books/{id}         - Get book by ID");
        System.out.println("     POST   /api/books              - Create a book");
        System.out.println("     PUT    /api/books/{id}         - Update a book");
        System.out.println("     DELETE /api/books/{id}         - Delete a book");
        System.out.println("     GET    /api/books/search?q=    - Search books");
        System.out.println("     GET    /api/books/author/{a}   - Books by author");
        System.out.println("     GET    /api/books/isbn/{isbn}  - Book by ISBN");
        System.out.println();
        System.out.println("  🗄️  H2 Console: http://localhost:8080/h2-console");
        System.out.println("     JDBC URL: jdbc:h2:mem:bookdb");
        System.out.println("     Username: sa (no password)");
        System.out.println("════════════════════════════════════════════════════════════");
    }
}

/*
 * ============================================================================
 * AUTO-CONFIGURATION DEEP DIVE
 * ============================================================================
 * 
 * When you add 'spring-boot-starter-web', Spring Boot auto-configures:
 * 
 * ✓ Embedded Tomcat server
 * ✓ DispatcherServlet (front controller)
 * ✓ Jackson for JSON serialization
 * ✓ Message converters
 * ✓ Static resource handling
 * ✓ Error pages
 * 
 * When you add 'spring-boot-starter-data-jpa', Spring Boot auto-configures:
 * 
 * ✓ DataSource (connection pool)
 * ✓ EntityManagerFactory (JPA)
 * ✓ TransactionManager
 * ✓ Spring Data JPA repositories
 * 
 * When you add 'h2' runtime dependency, Spring Boot auto-configures:
 * 
 * ✓ H2 in-memory database
 * ✓ H2 Console web interface
 * 
 * HOW TO SEE WHAT'S AUTO-CONFIGURED?
 * Add to application.properties:
 *   debug=true
 * 
 * You'll see a detailed report of:
 * - Positive matches (what was configured and why)
 * - Negative matches (what was NOT configured and why)
 * 
 * ============================================================================
 * CUSTOMIZING AUTO-CONFIGURATION
 * ============================================================================
 * 
 * Option 1: Properties (application.properties)
 *   server.port=9090
 *   spring.datasource.url=jdbc:mysql://localhost/mydb
 * 
 * Option 2: Provide your own beans
 *   @Bean
 *   public DataSource dataSource() {
 *       // Your custom DataSource
 *   }
 *   // Spring Boot will use YOUR bean instead of auto-configured one
 * 
 * Option 3: Exclude auto-configuration
 *   @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 */

