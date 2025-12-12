package com.learning.beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * ============================================================================
 *                    SPRING BEANS AND LIFECYCLE MANAGEMENT
 * ============================================================================
 * 
 * A BEAN is simply an object that is:
 * - Created by Spring
 * - Managed by Spring
 * - Stored in Spring's IoC Container (ApplicationContext)
 * 
 * Instead of YOU creating objects with "new", SPRING creates and manages them!
 * 
 * ============================================================================
 */

// ============================================================================
// PART 1: WHAT IS A BEAN?
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                        WITHOUT SPRING (Manual)                          │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  // YOU create and manage everything                                    │
 * │  BookRepository repo = new BookRepositoryImpl();                        │
 * │  BookService service = new BookService(repo);  // Manual wiring         │
 * │  BookController controller = new BookController(service);               │
 * │                                                                         │
 * │  Problems:                                                              │
 * │  - Tight coupling                                                       │
 * │  - Hard to test                                                         │
 * │  - Hard to swap implementations                                         │
 * │  - No lifecycle management                                              │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                        WITH SPRING (Beans)                              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  @Repository                                                            │
 * │  public class BookRepository { }     // Spring creates this             │
 * │                                                                         │
 * │  @Service                                                               │
 * │  public class BookService {                                             │
 * │      @Autowired                                                         │
 * │      private BookRepository repo;    // Spring injects this             │
 * │  }                                                                       │
 * │                                                                         │
 * │  @RestController                                                        │
 * │  public class BookController {                                          │
 * │      @Autowired                                                         │
 * │      private BookService service;    // Spring injects this             │
 * │  }                                                                       │
 * │                                                                         │
 * │  Benefits:                                                              │
 * │  ✅ Loose coupling                                                      │
 * │  ✅ Easy to test (mock dependencies)                                    │
 * │  ✅ Easy to swap implementations                                        │
 * │  ✅ Automatic lifecycle management                                      │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// PART 2: WAYS TO CREATE BEANS
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  METHOD 1: STEREOTYPE ANNOTATIONS (Most Common)                         │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  @Component     → Generic bean                                         │
 * │  @Service       → Business logic layer (same as @Component)            │
 * │  @Repository    → Data access layer (adds exception translation)       │
 * │  @Controller    → Web layer (MVC controller)                           │
 * │  @RestController → REST API controller (@Controller + @ResponseBody)   │
 * │                                                                         │
 * │  These are AUTO-DETECTED via component scanning!                        │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// Example: Creating beans with stereotype annotations
@Component
class GenericBean {
    // This becomes a Spring bean named "genericBean"
}

@Service
class MyService {
    // This becomes a Spring bean named "myService"
}

@Repository
class MyRepository {
    // This becomes a Spring bean named "myRepository"
}

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  METHOD 2: @Bean in @Configuration class                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  Use when:                                                              │
 * │  - You can't modify the class (third-party library)                    │
 * │  - You need custom initialization logic                                │
 * │  - You need to create multiple beans of same type                      │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

@Configuration
class AppConfig {
    
    // @Bean
    // public DataSource dataSource() {
    //     HikariDataSource ds = new HikariDataSource();
    //     ds.setJdbcUrl("jdbc:mysql://localhost:3306/db");
    //     ds.setUsername("user");
    //     ds.setPassword("pass");
    //     return ds;  // This becomes a bean named "dataSource"
    // }
    
    // Multiple beans of same type
    // @Bean("primaryDataSource")
    // public Object primaryDataSource() {
    //     return new Object(); // Primary database
    // }
    
    // @Bean("secondaryDataSource") 
    // public Object secondaryDataSource() {
    //     return new Object(); // Secondary database
    // }
}

// ============================================================================
// PART 3: BEAN SCOPES
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  BEAN SCOPES - How many instances are created?                          │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  SCOPE          │  INSTANCES  │  WHEN CREATED                          │
 * │  ──────────────────────────────────────────────────────────────────────│
 * │  singleton      │  ONE        │  At startup (default)                  │
 * │  prototype      │  MANY       │  Each time requested                   │
 * │  request        │  ONE/request│  Per HTTP request (web only)           │
 * │  session        │  ONE/session│  Per HTTP session (web only)           │
 * │  application    │  ONE/app    │  Per ServletContext (web only)         │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// SINGLETON (Default) - ONE instance shared by everyone
@Service
// @Scope("singleton")  ← This is the default, no need to specify
class SingletonService {
    // Only ONE instance exists
    // All @Autowired injections get the SAME instance
    // Created at application startup
}

// PROTOTYPE - NEW instance every time
@Service
@Scope("prototype")
class PrototypeService {
    // NEW instance created each time it's requested
    // @Autowired gives you a NEW instance each time
}

/*
 * SINGLETON vs PROTOTYPE:
 * 
 * SingletonService:
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                                                                         │
 * │  Controller A ──┐                                                       │
 * │                 ├──► SAME SingletonService instance                    │
 * │  Controller B ──┘                                                       │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * PrototypeService:
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                                                                         │
 * │  Controller A ────► PrototypeService instance #1                       │
 * │                                                                         │
 * │  Controller B ────► PrototypeService instance #2                       │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// Request scope - one per HTTP request
@Service
@Scope("request")
class RequestScopedService {
    // New instance for each HTTP request
    // Different users get different instances
    // Destroyed when request ends
}

// Session scope - one per HTTP session
@Service
@Scope("session")
class SessionScopedService {
    // One instance per user session
    // Same user = same instance
    // Destroyed when session expires
}

// ============================================================================
// PART 4: BEAN LIFECYCLE
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                    COMPLETE BEAN LIFECYCLE                              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    1. INSTANTIATION                              │   │
 * │  │                    (Spring calls constructor)                    │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    2. POPULATE PROPERTIES                        │   │
 * │  │                    (Dependency Injection)                        │   │
 * │  │                    @Autowired fields are set                     │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    3. BEAN NAME AWARE                            │   │
 * │  │                    setBeanName() if implements BeanNameAware     │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    4. BEAN FACTORY AWARE                         │   │
 * │  │                    setBeanFactory() if implements                │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    5. PRE-INITIALIZATION                         │   │
 * │  │                    BeanPostProcessor.postProcessBeforeInit()     │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    6. INITIALIZATION                             │   │
 * │  │                    @PostConstruct                                │   │
 * │  │                    InitializingBean.afterPropertiesSet()         │   │
 * │  │                    @Bean(initMethod = "init")                    │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    7. POST-INITIALIZATION                        │   │
 * │  │                    BeanPostProcessor.postProcessAfterInit()      │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    8. BEAN IS READY TO USE                       │   │
 * │  │                    ════════════════════════                      │   │
 * │  │                    Application runs...                           │   │
 * │  │                    ════════════════════════                      │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                              │                                          │
 * │                              ▼                                          │
 * │  ┌─────────────────────────────────────────────────────────────────┐   │
 * │  │                    9. DESTRUCTION                                │   │
 * │  │                    @PreDestroy                                   │   │
 * │  │                    DisposableBean.destroy()                      │   │
 * │  │                    @Bean(destroyMethod = "cleanup")              │   │
 * │  └─────────────────────────────────────────────────────────────────┘   │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// PART 5: DEPENDENCY INJECTION TYPES
// ============================================================================

/**
 * TYPE 1: CONSTRUCTOR INJECTION (Recommended!)
 */
@Service
class BookService {
    
    private final MyRepository bookRepository;  // final = immutable
    private final MyService notificationService;
    
    // Spring automatically injects dependencies
    // @Autowired is optional for single constructor (Spring 4.3+)
    public BookService(MyRepository bookRepository, MyService notificationService) {
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
    }
    
    /*
     * WHY CONSTRUCTOR INJECTION IS BEST:
     * ✅ Dependencies are final (immutable)
     * ✅ Dependencies are required (fails fast if missing)
     * ✅ Easy to test (just pass mocks in constructor)
     * ✅ Clear dependencies (visible in constructor)
     * ✅ No reflection needed
     */
}

/**
 * TYPE 2: FIELD INJECTION (Simple but not recommended)
 */
@Service
class ProductService {
    
    @Autowired
    private MyRepository productRepository;  // Injected directly into field
    
    @Autowired
    private MyService emailService;
    
    /*
     * WHY FIELD INJECTION IS NOT IDEAL:
     * ❌ Can't make fields final
     * ❌ Hard to test (need reflection or Spring context)
     * ❌ Hidden dependencies
     * ❌ Can create objects in invalid state
     */
}

/**
 * TYPE 3: SETTER INJECTION (For optional dependencies)
 */
@Service
class ReportService {
    
    private MyService cacheService;
    
    @Autowired(required = false)  // Optional dependency
    public void setCacheService(MyService cacheService) {
        this.cacheService = cacheService;
    }
    
    /*
     * USE SETTER INJECTION WHEN:
     * - Dependency is optional
     * - Need to change dependency at runtime
     * - Circular dependency issues (avoid if possible)
     */
}
/*
 * In application.properties:
 * mail.host=smtp.gmail.com
 * mail.port=587
 * mail.username=user@example.com
 */

/*
 * OUTPUT when application starts:
 * 
 * 1. Constructor called - poolSize is: 0
 * 2. @PostConstruct - poolSize is now: 10
 *    Creating 10 connections to jdbc:mysql://localhost:3306/db
 * 3. afterPropertiesSet() - pool ready
 * 
 * OUTPUT when application stops:
 * 
 * 4. @PreDestroy - starting cleanup
 * 5. destroy() - closing all connections
 */

// ============================================================================
// SUMMARY
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  BEAN ESSENTIALS CHEAT SHEET                                            │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  CREATE BEANS:                                                          │
 * │  • @Component, @Service, @Repository, @Controller                      │
 * │  • @Bean in @Configuration class                                       │
 * │                                                                         │
 * │  SCOPES:                                                                │
 * │  • singleton (default) - one instance                                  │
 * │  • prototype - new instance each time                                  │
 * │  • request/session - web scopes                                        │
 * │                                                                         │
 * │  LIFECYCLE CALLBACKS:                                                   │
 * │  • @PostConstruct - after injection, before ready                      │
 * │  • @PreDestroy - before bean destroyed                                 │
 * │                                                                         │
 * │  INJECTION:                                                             │
 * │  • Constructor injection (recommended)                                 │
 * │  • Field injection (@Autowired on field)                               │
 * │  • Setter injection                                                    │
 * │                                                                         │
 * │  QUALIFIERS:                                                            │
 * │  • @Qualifier("beanName") - choose specific bean                       │
 * │  • @Primary - default when multiple exist                              │
 * │                                                                         │
 * │  OTHER:                                                                 │
 * │  • @Lazy - delay creation until first use                              │
 * │  • @Value("${prop}") - inject config values                            │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// 2004-2006: XML wiring (painful!)
//      ↓
// 2007: @Autowired introduced (revolutionary!)
//      ↓
// 2007-2016: Field injection popular (easy but flawed)
//      ↓
// 2016: Spring 4.3 - Constructor injection made easy
//       (no @Autowired needed for single constructor)
//      ↓
// Today: Constructor injection recommended
//        @Autowired still used for special cases