package com.learning.spring.era2_spring;

/**
 * ============================================================================
 * ERA 2: SPRING FRAMEWORK (2003-2014)
 * ============================================================================
 * 
 * Spring Framework brought revolutionary concepts:
 * - Inversion of Control (IoC)
 * - Dependency Injection (DI)
 * - Aspect-Oriented Programming (AOP)
 * - Declarative Transaction Management
 * - JPA/Hibernate Integration (YES, JPA existed!)
 * 
 * ⚠️ WHAT SPRING FRAMEWORK SOLVED vs SERVLETS:
 * ✅ Dependency Injection - no more "new" everywhere
 * ✅ Separation of concerns - Controller, Service, Repository layers
 * ✅ Transaction management via @Transactional
 * ✅ JPA/Hibernate integration - no raw JDBC
 * ✅ Annotation-based request mapping
 * ✅ Testability - easy to mock dependencies
 * 
 * ❌ WHAT PROBLEMS STILL REMAINED (solved by Spring Boot):
 * ❌ Had to write DAO/Repository IMPLEMENTATION classes manually
 * ❌ XML configuration was verbose (100s of lines)
 * ❌ Still needed external Tomcat/JBoss server
 * ❌ Manual dependency version management nightmare
 * ❌ web.xml still required
 * ❌ No auto-configuration - configure EVERYTHING manually
 * 
 * This file shows ACCURATE Spring Framework code from that era.
 * NOTE: This is EDUCATIONAL CODE ONLY - not meant to be run in this project.
 */

import java.util.List;
import java.util.Optional;

// ============================================================================
// LAYER 1: ENTITY - Same as today (JPA existed in Spring Framework era!)
// ============================================================================

/**
 * JPA Entity - This part was similar to modern Spring Boot.
 * JPA annotations were available and worked the same way.
 * 
 * In real code, you'd have:
 * @Entity
 * @Table(name = "books")
 */
class Era2Book {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // @Column(nullable = false)
    private String title;
    
    // @Column(nullable = false)
    private String author;
    
    // @Column(nullable = false)
    private Double price;
    
    // Default constructor required by JPA
    public Era2Book() {}
    
    public Era2Book(String title, String author, Double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }
    
    // Getters and setters - had to write manually (no Lombok commonly used)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}

// ============================================================================
// LAYER 2: REPOSITORY - THE KEY DIFFERENCE FROM SPRING BOOT!
// ============================================================================

/**
 * ❌ PROBLEM THAT SPRING BOOT SOLVED:
 * 
 * In Spring Framework, you had to:
 * 1. Create an INTERFACE for your repository
 * 2. Create an IMPLEMENTATION class manually
 * 3. Write ALL the JPA/Hibernate code yourself
 * 4. Inject EntityManager manually
 * 
 * In Spring Boot with Spring Data JPA:
 * - You just create an interface extending JpaRepository
 * - Spring creates the implementation AUTOMATICALLY!
 */

// Step 1: Define the interface
interface Era2BookRepository {
    List<Era2Book> findAll();
    Era2Book findById(Long id);
    Era2Book save(Era2Book book);
    void deleteById(Long id);
    List<Era2Book> findByAuthor(String author);
}

/**
 * Step 2: YOU HAD TO WRITE THE IMPLEMENTATION YOURSELF!
 * 
 * This is what Spring Boot's Spring Data JPA eliminated.
 * All this boilerplate code is now generated automatically!
 * 
 * In real code: @Repository
 */
class Era2BookRepositoryImpl implements Era2BookRepository {
    
    // In real code: @PersistenceContext
    // private EntityManager entityManager;
    
    // Constructor injection (or field injection with @Autowired)
    // public Era2BookRepositoryImpl(EntityManager entityManager) {
    //     this.entityManager = entityManager;
    // }
    
    @Override
    public List<Era2Book> findAll() {
        // YOU HAD TO WRITE THIS JPQL QUERY MANUALLY!
        // return entityManager.createQuery("SELECT b FROM Book b", Era2Book.class)
        //                     .getResultList();
        return List.of();
    }
    
    @Override
    public Era2Book findById(Long id) {
        // YOU HAD TO WRITE THIS MANUALLY!
        // return entityManager.find(Era2Book.class, id);
        return null;
    }
    
    @Override
    public Era2Book save(Era2Book book) {
        // YOU HAD TO WRITE THE SAVE LOGIC MANUALLY!
        // if (book.getId() == null) {
        //     entityManager.persist(book);
        //     return book;
        // } else {
        //     return entityManager.merge(book);
        // }
        return book;
    }
    
    @Override
    public void deleteById(Long id) {
        // YOU HAD TO WRITE DELETE LOGIC MANUALLY!
        // Era2Book book = entityManager.find(Era2Book.class, id);
        // if (book != null) {
        //     entityManager.remove(book);
        // }
    }
    
    @Override
    public List<Era2Book> findByAuthor(String author) {
        // EVERY CUSTOM QUERY HAD TO BE WRITTEN MANUALLY!
        // return entityManager.createQuery(
        //         "SELECT b FROM Book b WHERE b.author = :author", Era2Book.class)
        //     .setParameter("author", author)
        //     .getResultList();
        return List.of();
    }
    
    // If you needed more queries, you had to write EACH ONE:
    
    public List<Era2Book> findByPriceLessThan(Double price) {
        // return entityManager.createQuery(
        //         "SELECT b FROM Book b WHERE b.price < :price", Era2Book.class)
        //     .setParameter("price", price)
        //     .getResultList();
        return List.of();
    }
    
    public List<Era2Book> findByTitleContaining(String keyword) {
        // return entityManager.createQuery(
        //         "SELECT b FROM Book b WHERE b.title LIKE :keyword", Era2Book.class)
        //     .setParameter("keyword", "%" + keyword + "%")
        //     .getResultList();
        return List.of();
    }
    
    public Long countByAuthor(String author) {
        // return entityManager.createQuery(
        //         "SELECT COUNT(b) FROM Book b WHERE b.author = :author", Long.class)
        //     .setParameter("author", author)
        //     .getSingleResult();
        return 0L;
    }
    
    // IMAGINE WRITING 20+ SUCH METHODS FOR EACH ENTITY!
    // THAT'S WHAT SPRING DATA JPA ELIMINATED!
}

/*
 * ============================================================================
 * COMPARISON: Spring Framework vs Spring Boot Repository
 * ============================================================================
 * 
 * SPRING FRAMEWORK (what you had to write):
 * 
 * @Repository
 * public class BookRepositoryImpl implements BookRepository {
 *     
 *     @PersistenceContext
 *     private EntityManager entityManager;
 *     
 *     @Override
 *     public List<Book> findAll() {
 *         return entityManager.createQuery("SELECT b FROM Book b", Book.class)
 *                             .getResultList();
 *     }
 *     
 *     @Override
 *     public Book findById(Long id) {
 *         return entityManager.find(Book.class, id);
 *     }
 *     
 *     @Override
 *     public Book save(Book book) {
 *         if (book.getId() == null) {
 *             entityManager.persist(book);
 *             return book;
 *         } else {
 *             return entityManager.merge(book);
 *         }
 *     }
 *     
 *     @Override
 *     public void deleteById(Long id) {
 *         Book book = entityManager.find(Book.class, id);
 *         if (book != null) {
 *             entityManager.remove(book);
 *         }
 *     }
 *     
 *     @Override
 *     public List<Book> findByAuthor(String author) {
 *         return entityManager.createQuery(
 *                 "SELECT b FROM Book b WHERE b.author = :author", Book.class)
 *             .setParameter("author", author)
 *             .getResultList();
 *     }
 *     
 *     // 50+ lines just for basic CRUD!
 * }
 * 
 * ============================================================================
 * 
 * SPRING BOOT with Spring Data JPA (what you write now):
 * 
 * @Repository
 * public interface BookRepository extends JpaRepository<Book, Long> {
 *     List<Book> findByAuthor(String author);
 *     // THAT'S IT! Everything else is auto-generated!
 * }
 * 
 * Lines of code: 50+ vs 3!
 * 
 * Spring Data JPA automatically provides:
 * - save(), findById(), findAll(), deleteById(), count(), existsById()
 * - Pagination and sorting
 * - Custom queries from method names (findByAuthor, findByPriceLessThan, etc.)
 * - @Query for complex queries
 * 
 * ============================================================================
 */

// ============================================================================
// LAYER 3: SERVICE - Similar to modern Spring Boot (this was already good!)
// ============================================================================

/**
 * The Service layer was already clean in Spring Framework.
 * @Transactional worked the same way.
 * 
 * In real code: @Service
 */
class Era2BookService {
    
    // In real code: @Autowired
    private Era2BookRepository bookRepository;
    
    // Constructor injection was already the recommended approach
    public Era2BookService(Era2BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    // @Transactional(readOnly = true)
    public List<Era2Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    // @Transactional(readOnly = true)
    public Era2Book getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    // @Transactional
    public Era2Book createBook(Era2Book book) {
        return bookRepository.save(book);
    }
    
    // @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    // @Transactional(readOnly = true)
    public List<Era2Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }
}

// ============================================================================
// LAYER 4: CONTROLLER - Similar but needed @ResponseBody everywhere
// ============================================================================

/**
 * Controllers existed but:
 * - Needed @ResponseBody on EVERY method (no @RestController yet, came in Spring 4)
 * - Or use @Controller + @ResponseBody
 * - JSON conversion required explicit Jackson configuration in XML
 * 
 * In real code: 
 * @Controller
 * @RequestMapping("/api/books")
 */
class Era2BookController {
    
    // @Autowired
    private Era2BookService bookService;
    
    public Era2BookController(Era2BookService bookService) {
        this.bookService = bookService;
    }
    
    // @GetMapping  (or @RequestMapping(method = RequestMethod.GET) in older Spring)
    // @ResponseBody  ← Had to add this to EVERY method!
    public List<Era2Book> getAllBooks() {
        return bookService.getAllBooks();
    }
    
    // @GetMapping("/{id}")
    // @ResponseBody
    public Era2Book getBookById(Long id) {  // @PathVariable Long id
        return bookService.getBookById(id);
    }
    
    // @PostMapping
    // @ResponseBody
    public Era2Book createBook(Era2Book book) {  // @RequestBody Book book
        return bookService.createBook(book);
    }
    
    // @DeleteMapping("/{id}")
    // @ResponseBody
    public void deleteBook(Long id) {  // @PathVariable Long id
        bookService.deleteBook(id);
    }
}

// ============================================================================
// THE REMAINING PAIN: XML CONFIGURATION
// ============================================================================

/*
 * ❌ PROBLEM: You needed MULTIPLE XML configuration files!
 * 
 * Spring Boot replaced ALL of this with:
 * 1. @SpringBootApplication annotation
 * 2. application.properties (simple key-value)
 * 3. Auto-configuration based on classpath
 */

/*
 * ============== persistence.xml (JPA Configuration) ==============
 * You needed this file in META-INF/ folder!
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <persistence xmlns="http://java.sun.com/xml/ns/persistence" version="2.0">
 *     <persistence-unit name="bookPU" transaction-type="RESOURCE_LOCAL">
 *         <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
 *         <class>com.example.entity.Book</class>
 *         <properties>
 *             <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
 *             <property name="hibernate.show_sql" value="true"/>
 *             <property name="hibernate.format_sql" value="true"/>
 *             <property name="hibernate.hbm2ddl.auto" value="update"/>
 *         </properties>
 *     </persistence-unit>
 * </persistence>
 */

/*
 * ============== applicationContext.xml ==============
 * Main Spring configuration - often 100+ lines!
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *        xmlns:context="http://www.springframework.org/schema/context"
 *        xmlns:tx="http://www.springframework.org/schema/tx"
 *        xmlns:jpa="http://www.springframework.org/schema/data/jpa"
 *        xsi:schemaLocation="...">
 *     
 *     <!-- Enable component scanning -->
 *     <context:component-scan base-package="com.example"/>
 *     
 *     <!-- DataSource - Connection Pool Configuration -->
 *     <bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
 *         <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
 *         <property name="url" value="jdbc:mysql://localhost:3306/bookdb"/>
 *         <property name="username" value="root"/>
 *         <property name="password" value="password"/>
 *         <property name="initialSize" value="5"/>
 *         <property name="maxTotal" value="10"/>
 *     </bean>
 *     
 *     <!-- JPA EntityManagerFactory -->
 *     <bean id="entityManagerFactory" 
 *           class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
 *         <property name="dataSource" ref="dataSource"/>
 *         <property name="packagesToScan" value="com.example.entity"/>
 *         <property name="jpaVendorAdapter">
 *             <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
 *                 <property name="showSql" value="true"/>
 *                 <property name="generateDdl" value="true"/>
 *                 <property name="databasePlatform" value="org.hibernate.dialect.MySQLDialect"/>
 *             </bean>
 *         </property>
 *         <property name="jpaProperties">
 *             <props>
 *                 <prop key="hibernate.format_sql">true</prop>
 *                 <prop key="hibernate.hbm2ddl.auto">update</prop>
 *             </props>
 *         </property>
 *     </bean>
 *     
 *     <!-- Transaction Manager -->
 *     <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
 *         <property name="entityManagerFactory" ref="entityManagerFactory"/>
 *     </bean>
 *     
 *     <!-- Enable @Transactional annotation -->
 *     <tx:annotation-driven transaction-manager="transactionManager"/>
 *     
 * </beans>
 */

/*
 * ============== spring-mvc.xml ==============
 * MVC Configuration - separate file!
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *        xmlns:mvc="http://www.springframework.org/schema/mvc"
 *        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *        xsi:schemaLocation="...">
 *     
 *     <!-- Enable annotation-driven MVC -->
 *     <mvc:annotation-driven>
 *         <mvc:message-converters>
 *             <!-- You had to configure Jackson explicitly for JSON! -->
 *             <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
 *                 <property name="objectMapper">
 *                     <bean class="com.fasterxml.jackson.databind.ObjectMapper">
 *                         <property name="serializationInclusion" value="NON_NULL"/>
 *                     </bean>
 *                 </property>
 *             </bean>
 *         </mvc:message-converters>
 *     </mvc:annotation-driven>
 *     
 *     <!-- Static resources -->
 *     <mvc:resources mapping="/static/**" location="/static/"/>
 *     
 *     <!-- View Resolver -->
 *     <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
 *         <property name="prefix" value="/WEB-INF/views/"/>
 *         <property name="suffix" value=".jsp"/>
 *     </bean>
 * </beans>
 */

/*
 * ============== web.xml (STILL REQUIRED!) ==============
 * Deployment descriptor - needed for Servlet container
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="3.1">
 *     
 *     <!-- Spring Context Loader -->
 *     <context-param>
 *         <param-name>contextConfigLocation</param-name>
 *         <param-value>/WEB-INF/applicationContext.xml</param-value>
 *     </context-param>
 *     
 *     <listener>
 *         <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
 *     </listener>
 *     
 *     <!-- Spring MVC DispatcherServlet -->
 *     <servlet>
 *         <servlet-name>dispatcher</servlet-name>
 *         <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 *         <init-param>
 *             <param-name>contextConfigLocation</param-name>
 *             <param-value>/WEB-INF/spring-mvc.xml</param-value>
 *         </init-param>
 *         <load-on-startup>1</load-on-startup>
 *     </servlet>
 *     
 *     <servlet-mapping>
 *         <servlet-name>dispatcher</servlet-name>
 *         <url-pattern>/</url-pattern>
 *     </servlet-mapping>
 *     
 * </web-app>
 */

// ============================================================================
// THE UGLY: DEPENDENCY VERSION MANAGEMENT
// ============================================================================

/*
 * In Maven pom.xml, you had to manage EVERY version manually:
 * One wrong version = hours of debugging ClassNotFoundException!
 * 
 * <dependencies>
 *     <!-- Spring Framework - ALL must be same version! -->
 *     <dependency>
 *         <groupId>org.springframework</groupId>
 *         <artifactId>spring-core</artifactId>
 *         <version>4.3.18.RELEASE</version>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework</groupId>
 *         <artifactId>spring-context</artifactId>
 *         <version>4.3.18.RELEASE</version>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework</groupId>
 *         <artifactId>spring-web</artifactId>
 *         <version>4.3.18.RELEASE</version>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework</groupId>
 *         <artifactId>spring-webmvc</artifactId>
 *         <version>4.3.18.RELEASE</version>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework</groupId>
 *         <artifactId>spring-orm</artifactId>
 *         <version>4.3.18.RELEASE</version>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework</groupId>
 *         <artifactId>spring-tx</artifactId>
 *         <version>4.3.18.RELEASE</version>
 *     </dependency>
 *     
 *     <!-- JPA API -->
 *     <dependency>
 *         <groupId>javax.persistence</groupId>
 *         <artifactId>javax.persistence-api</artifactId>
 *         <version>2.2</version>
 *     </dependency>
 *     
 *     <!-- Hibernate - must be compatible with Spring version! -->
 *     <dependency>
 *         <groupId>org.hibernate</groupId>
 *         <artifactId>hibernate-core</artifactId>
 *         <version>5.2.17.Final</version>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.hibernate</groupId>
 *         <artifactId>hibernate-entitymanager</artifactId>
 *         <version>5.2.17.Final</version>
 *     </dependency>
 *     
 *     <!-- Jackson for JSON - must be compatible! -->
 *     <dependency>
 *         <groupId>com.fasterxml.jackson.core</groupId>
 *         <artifactId>jackson-databind</artifactId>
 *         <version>2.9.6</version>
 *     </dependency>
 *     
 *     <!-- Database connection pool -->
 *     <dependency>
 *         <groupId>org.apache.commons</groupId>
 *         <artifactId>commons-dbcp2</artifactId>
 *         <version>2.5.0</version>
 *     </dependency>
 *     
 *     <!-- MySQL Driver -->
 *     <dependency>
 *         <groupId>mysql</groupId>
 *         <artifactId>mysql-connector-java</artifactId>
 *         <version>8.0.15</version>
 *     </dependency>
 *     
 *     <!-- Servlet API -->
 *     <dependency>
 *         <groupId>javax.servlet</groupId>
 *         <artifactId>javax.servlet-api</artifactId>
 *         <version>3.1.0</version>
 *         <scope>provided</scope>
 *     </dependency>
 *     
 * </dependencies>
 * 
 * Spring Boot replaces ALL of this with:
 * 
 * dependencies {
 *     implementation 'org.springframework.boot:spring-boot-starter-web'
 *     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
 *     runtimeOnly 'mysql:mysql-connector-java'
 * }
 * 
 * 3 lines vs 50+ lines, and versions are AUTOMATICALLY compatible!
 */

// ============================================================================
// DEPLOYMENT PROCESS - Another Pain Point!
// ============================================================================

/*
 * SPRING FRAMEWORK DEPLOYMENT:
 * 1. Write all the code
 * 2. Configure all XML files correctly
 * 3. Build WAR file: mvn clean package
 * 4. Install Tomcat/JBoss separately
 * 5. Copy WAR to Tomcat's webapps/ directory
 * 6. Start Tomcat: ./catalina.sh run
 * 7. Wait for deployment...
 * 8. Check logs for errors
 * 9. If error, stop Tomcat, fix, rebuild WAR, redeploy
 * 
 * SPRING BOOT DEPLOYMENT:
 * 1. Write code
 * 2. Run: ./gradlew bootRun
 * 3. Done! (Embedded Tomcat starts automatically)
 * 
 * Or for production:
 * 1. Build JAR: ./gradlew bootJar
 * 2. Run: java -jar app.jar
 * 3. Done! (No external server needed)
 */

// ============================================================================
// SUMMARY: SPRING FRAMEWORK ERA
// ============================================================================

/*
 * ✅ WHAT SPRING FRAMEWORK SOLVED (vs Servlets):
 * 
 * 1. Dependency Injection
 *    - Servlet: BookServlet creates new BookDAO() directly
 *    - Spring: @Autowired BookRepository bookRepository
 * 
 * 2. Transaction Management
 *    - Servlet: Manual try/catch/commit/rollback
 *    - Spring: @Transactional annotation
 * 
 * 3. JPA/Hibernate Integration
 *    - Servlet: Raw JDBC, manual ResultSet mapping
 *    - Spring: EntityManager, JPQL queries
 * 
 * 4. MVC Pattern
 *    - Servlet: doGet(), doPost() in one servlet
 *    - Spring: @Controller, @GetMapping, @PostMapping
 * 
 * 5. Separation of Concerns
 *    - Servlet: Everything in one class
 *    - Spring: Controller → Service → Repository layers
 * 
 * ============================================================================
 * 
 * ❌ WHAT PROBLEMS REMAINED (solved by Spring Boot):
 * 
 * 1. DAO/Repository Implementation
 *    - Spring: Write 50+ lines per repository implementation
 *    - Spring Boot: Just extend JpaRepository (3 lines!)
 * 
 * 2. XML Configuration Hell
 *    - Spring: 3-4 XML files, 200+ lines total
 *    - Spring Boot: application.properties (10 lines)
 * 
 * 3. External Server Required
 *    - Spring: Install & configure Tomcat/JBoss
 *    - Spring Boot: Embedded Tomcat, just run!
 * 
 * 4. Version Management
 *    - Spring: Manually match 15+ library versions
 *    - Spring Boot: Starters handle all versions
 * 
 * 5. Auto-Configuration
 *    - Spring: Configure EVERYTHING manually
 *    - Spring Boot: Smart defaults based on classpath
 * 
 * 6. Development Speed
 *    - Spring: Build WAR → Deploy → Test → Repeat
 *    - Spring Boot: Just run, hot-reload with DevTools
 */
