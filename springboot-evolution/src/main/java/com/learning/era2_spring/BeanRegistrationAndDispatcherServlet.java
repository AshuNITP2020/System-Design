package com.learning.era2_spring;

/**
 * ============================================================================
 * SPRING FRAMEWORK: BEAN REGISTRATION & DISPATCHER SERVLET
 * ============================================================================
 * 
 * This file explains TWO major configuration requirements in Spring Framework
 * that Spring Boot eliminated:
 * 
 * 1. BEAN REGISTRATION - How Spring knows about your classes
 * 2. DISPATCHER SERVLET - The Front Controller for web requests
 * 
 * ============================================================================
 */

// ============================================================================
// PART 1: BEAN REGISTRATION - "How does Spring know about my classes?"
// ============================================================================

/*
 * In Spring Framework, you had TWO ways to register beans:
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  METHOD 1: XML Bean Registration (Original Way - Spring 1.x/2.x)       │
 * │  Every single bean had to be declared in XML!                          │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * applicationContext.xml:
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *        xsi:schemaLocation="http://www.springframework.org/schema/beans
 *                            http://www.springframework.org/schema/beans/spring-beans.xsd">
 *     
 *     <!-- EVERY BEAN HAD TO BE REGISTERED MANUALLY! -->
 *     
 *     <!-- Register BookRepository -->
 *     <bean id="bookRepository" class="com.example.repository.BookRepositoryImpl">
 *         <property name="entityManager" ref="entityManager"/>
 *     </bean>
 *     
 *     <!-- Register BookService and inject its dependencies -->
 *     <bean id="bookService" class="com.example.service.BookService">
 *         <property name="bookRepository" ref="bookRepository"/>
 *     </bean>
 *     
 *     <!-- Register BookController and inject its dependencies -->
 *     <bean id="bookController" class="com.example.controller.BookController">
 *         <property name="bookService" ref="bookService"/>
 *     </bean>
 *     
 *     <!-- If you had 50 classes, you needed 50 <bean> entries! -->
 *     
 *     <!-- You could also use constructor injection -->
 *     <bean id="userService" class="com.example.service.UserService">
 *         <constructor-arg ref="userRepository"/>
 *         <constructor-arg ref="emailService"/>
 *     </bean>
 *     
 * </beans>
 * 
 * PAIN POINTS:
 * - Add a new class? Edit XML file
 * - Change a dependency? Edit XML file
 * - Typo in class name? Runtime error, not compile-time!
 * - Refactor package name? Update ALL XML entries
 */

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  METHOD 2: Annotation + Component Scan (Spring 2.5+, 2007)             │
 * │  Better, but still needed XML for component-scan configuration!        │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * The annotations (@Component, @Service, @Repository, @Controller) were 
 * introduced in Spring 2.5, but you STILL needed XML to enable scanning!
 * 
 * applicationContext.xml (still required!):
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *        xmlns:context="http://www.springframework.org/schema/context"
 *        xsi:schemaLocation="...">
 *     
 *     <!-- This line tells Spring to scan for @Component, @Service, etc. -->
 *     <!-- WITHOUT THIS, ANNOTATIONS DON'T WORK! -->
 *     <context:component-scan base-package="com.example"/>
 *     
 *     <!-- You still needed to register infrastructure beans in XML -->
 *     <!-- DataSource, EntityManagerFactory, TransactionManager, etc. -->
 *     
 * </beans>
 * 
 * Then in your Java code, you could use annotations:
 */

// Example of annotation-based bean registration (Spring 2.5+)
// @Repository  ← Marks as Spring bean (Repository layer)
class AnnotatedBookRepository {
    // Spring would create and manage this bean
}

// @Service  ← Marks as Spring bean (Service layer)
class AnnotatedBookService {
    // @Autowired  ← Spring injects the dependency
    private AnnotatedBookRepository bookRepository;
}

// @Controller  ← Marks as Spring bean (Web layer)
class AnnotatedBookController {
    // @Autowired
    private AnnotatedBookService bookService;
}

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  INFRASTRUCTURE BEANS - Always needed XML or Java Config!              │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * Even with component-scan, these STILL required manual configuration:
 * 
 * <!-- DataSource - Database connection pool -->
 * <bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
 *     <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
 *     <property name="url" value="jdbc:mysql://localhost:3306/bookdb"/>
 *     <property name="username" value="root"/>
 *     <property name="password" value="password"/>
 * </bean>
 * 
 * <!-- EntityManagerFactory - JPA setup -->
 * <bean id="entityManagerFactory" 
 *       class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
 *     <property name="dataSource" ref="dataSource"/>
 *     <property name="packagesToScan" value="com.example.entity"/>
 *     <property name="jpaVendorAdapter">
 *         <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"/>
 *     </property>
 * </bean>
 * 
 * <!-- TransactionManager -->
 * <bean id="transactionManager" 
 *       class="org.springframework.orm.jpa.JpaTransactionManager">
 *     <property name="entityManagerFactory" ref="entityManagerFactory"/>
 * </bean>
 * 
 * Spring Boot auto-configures ALL of these based on your dependencies!
 */

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  METHOD 3: Java Config (Spring 3.0+, 2009)                             │
 * │  XML-free, but still lots of manual configuration!                     │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * Spring 3.0 introduced @Configuration classes to replace XML:
 */

// @Configuration  ← This class contains bean definitions
// @ComponentScan(basePackages = "com.example")  ← Enable annotation scanning
class AppConfig {
    
    // @Bean  ← Register this method's return value as a Spring bean
    // @Bean
    public Object dataSource() {
        // BasicDataSource dataSource = new BasicDataSource();
        // dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // dataSource.setUrl("jdbc:mysql://localhost:3306/bookdb");
        // dataSource.setUsername("root");
        // dataSource.setPassword("password");
        // return dataSource;
        return null;
    }
    
    // @Bean
    public Object entityManagerFactory() {
        // LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        // em.setDataSource(dataSource());
        // em.setPackagesToScan("com.example.entity");
        // em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // return em;
        return null;
    }
    
    // @Bean
    public Object transactionManager() {
        // JpaTransactionManager tm = new JpaTransactionManager();
        // tm.setEntityManagerFactory(entityManagerFactory().getObject());
        // return tm;
        return null;
    }
    
    // Still had to configure EVERY infrastructure bean!
}

/*
 * ============================================================================
 * COMPARISON: Bean Registration
 * ============================================================================
 * 
 * SPRING FRAMEWORK (any of the 3 methods):
 * - XML: 50+ lines for infrastructure beans
 * - Java Config: 30+ lines for same configuration
 * - Always needed SOME form of explicit configuration
 * 
 * SPRING BOOT:
 * 
 * @SpringBootApplication  ← This single annotation does EVERYTHING!
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * 
 * @SpringBootApplication includes:
 * - @Configuration (this is a config class)
 * - @EnableAutoConfiguration (auto-configure based on classpath)
 * - @ComponentScan (scan for @Component, @Service, etc.)
 * 
 * Plus application.properties:
 * spring.datasource.url=jdbc:mysql://localhost:3306/bookdb
 * spring.datasource.username=root
 * spring.datasource.password=password
 * 
 * That's it! Spring Boot auto-configures DataSource, EntityManagerFactory,
 * TransactionManager, and everything else!
 */

// ============================================================================
// PART 2: DISPATCHER SERVLET - "How do HTTP requests reach my controllers?"
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  WHAT IS DISPATCHER SERVLET?                                           │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * DispatcherServlet is Spring MVC's FRONT CONTROLLER.
 * 
 * HTTP Request Flow:
 * 
 *     Browser                    Tomcat                     Your Code
 *        │                          │                           │
 *        │  GET /api/books          │                           │
 *        │ ──────────────────────►  │                           │
 *        │                          │                           │
 *        │                   ┌──────┴──────┐                    │
 *        │                   │ Dispatcher  │                    │
 *        │                   │  Servlet    │                    │
 *        │                   │             │                    │
 *        │                   │  1. Receive │                    │
 *        │                   │     request │                    │
 *        │                   │             │                    │
 *        │                   │  2. Find    │                    │
 *        │                   │   handler   │ ──────────────────►│ @GetMapping
 *        │                   │             │                    │ handler
 *        │                   │  3. Invoke  │                    │
 *        │                   │   handler   │◄───────────────────│ returns data
 *        │                   │             │                    │
 *        │                   │  4. Convert │                    │
 *        │                   │   to JSON   │                    │
 *        │                   └──────┬──────┘                    │
 *        │  {"books": [...]}        │                           │
 *        │ ◄──────────────────────  │                           │
 * 
 * WITHOUT DispatcherServlet, Spring MVC doesn't work!
 */

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  CONFIGURING DISPATCHER SERVLET IN SPRING FRAMEWORK                    │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * You MUST configure DispatcherServlet in web.xml:
 * 
 * WEB-INF/web.xml:
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="3.1">
 *     
 *     <!-- ============================================================ -->
 *     <!-- STEP 1: Load Root Application Context                        -->
 *     <!-- This loads your main Spring configuration (services, repos)  -->
 *     <!-- ============================================================ -->
 *     
 *     <context-param>
 *         <param-name>contextConfigLocation</param-name>
 *         <param-value>/WEB-INF/applicationContext.xml</param-value>
 *     </context-param>
 *     
 *     <!-- ContextLoaderListener creates the root ApplicationContext -->
 *     <listener>
 *         <listener-class>
 *             org.springframework.web.context.ContextLoaderListener
 *         </listener-class>
 *     </listener>
 *     
 *     <!-- ============================================================ -->
 *     <!-- STEP 2: Configure DispatcherServlet                          -->
 *     <!-- This is the Front Controller for Spring MVC                  -->
 *     <!-- ============================================================ -->
 *     
 *     <servlet>
 *         <servlet-name>dispatcher</servlet-name>
 *         <servlet-class>
 *             org.springframework.web.servlet.DispatcherServlet
 *         </servlet-class>
 *         
 *         <!-- Load MVC-specific configuration -->
 *         <init-param>
 *             <param-name>contextConfigLocation</param-name>
 *             <param-value>/WEB-INF/spring-mvc.xml</param-value>
 *         </init-param>
 *         
 *         <!-- Load on startup, not on first request -->
 *         <load-on-startup>1</load-on-startup>
 *     </servlet>
 *     
 *     <!-- Map ALL requests to DispatcherServlet -->
 *     <servlet-mapping>
 *         <servlet-name>dispatcher</servlet-name>
 *         <url-pattern>/</url-pattern>
 *     </servlet-mapping>
 *     
 *     <!-- ============================================================ -->
 *     <!-- OPTIONAL: Character encoding filter                          -->
 *     <!-- ============================================================ -->
 *     
 *     <filter>
 *         <filter-name>characterEncodingFilter</filter-name>
 *         <filter-class>
 *             org.springframework.web.filter.CharacterEncodingFilter
 *         </filter-class>
 *         <init-param>
 *             <param-name>encoding</param-name>
 *             <param-value>UTF-8</param-value>
 *         </init-param>
 *     </filter>
 *     
 *     <filter-mapping>
 *         <filter-name>characterEncodingFilter</filter-name>
 *         <url-pattern>/*</url-pattern>
 *     </filter-mapping>
 *     
 * </web-app>
 * 
 * THAT'S 50+ LINES JUST FOR BASIC SETUP!
 */

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  spring-mvc.xml - DispatcherServlet's Configuration                    │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * DispatcherServlet loads its own ApplicationContext from spring-mvc.xml:
 * 
 * WEB-INF/spring-mvc.xml:
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *        xmlns:mvc="http://www.springframework.org/schema/mvc"
 *        xmlns:context="http://www.springframework.org/schema/context"
 *        xsi:schemaLocation="...">
 *     
 *     <!-- Enable @Controller, @RequestMapping, etc. -->
 *     <mvc:annotation-driven>
 *         <!-- Configure JSON message converter -->
 *         <mvc:message-converters>
 *             <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
 *                 <property name="objectMapper">
 *                     <bean class="com.fasterxml.jackson.databind.ObjectMapper">
 *                         <!-- Configure Jackson -->
 *                         <property name="serializationInclusion" value="NON_NULL"/>
 *                     </bean>
 *                 </property>
 *             </bean>
 *         </mvc:message-converters>
 *     </mvc:annotation-driven>
 *     
 *     <!-- Scan for @Controller classes -->
 *     <context:component-scan base-package="com.example.controller"/>
 *     
 *     <!-- View Resolver for JSP (if using JSP) -->
 *     <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
 *         <property name="prefix" value="/WEB-INF/views/"/>
 *         <property name="suffix" value=".jsp"/>
 *     </bean>
 *     
 *     <!-- Static resources -->
 *     <mvc:resources mapping="/static/**" location="/static/"/>
 *     
 *     <!-- Multipart resolver for file uploads -->
 *     <bean id="multipartResolver" 
 *           class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
 *         <property name="maxUploadSize" value="10485760"/>
 *     </bean>
 *     
 * </beans>
 * 
 * ANOTHER 30+ LINES OF CONFIGURATION!
 */

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Java-based Configuration (Spring 3.1+) - No web.xml!                  │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * Spring 3.1 introduced WebApplicationInitializer to replace web.xml:
 */

// public class WebAppInitializer implements WebApplicationInitializer {
//     
//     @Override
//     public void onStartup(ServletContext servletContext) {
//         
//         // Create root application context
//         AnnotationConfigWebApplicationContext rootContext = 
//             new AnnotationConfigWebApplicationContext();
//         rootContext.register(AppConfig.class);
//         
//         // Create ContextLoaderListener
//         servletContext.addListener(new ContextLoaderListener(rootContext));
//         
//         // Create MVC application context
//         AnnotationConfigWebApplicationContext mvcContext = 
//             new AnnotationConfigWebApplicationContext();
//         mvcContext.register(MvcConfig.class);
//         
//         // Register DispatcherServlet
//         ServletRegistration.Dynamic dispatcher = 
//             servletContext.addServlet("dispatcher", new DispatcherServlet(mvcContext));
//         dispatcher.setLoadOnStartup(1);
//         dispatcher.addMapping("/");
//     }
// }

// @Configuration
// @EnableWebMvc  // Enable Spring MVC
// @ComponentScan("com.example.controller")
// class MvcConfig implements WebMvcConfigurer {
//     
//     @Override
//     public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//         // Configure Jackson for JSON
//         converters.add(new MappingJackson2HttpMessageConverter());
//     }
//     
//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         // Static resources
//         registry.addResourceHandler("/static/**")
//                 .addResourceLocations("/static/");
//     }
// }

/*
 * Still 40+ lines of configuration code!
 */

/*
 * ============================================================================
 * COMPARISON: DispatcherServlet Configuration
 * ============================================================================
 * 
 * SPRING FRAMEWORK:
 * 
 * Files needed:
 * - web.xml (50 lines) OR WebApplicationInitializer (30 lines)
 * - spring-mvc.xml (30 lines) OR MvcConfig (20 lines)
 * - applicationContext.xml (50 lines) OR AppConfig (30 lines)
 * 
 * Total: 80-130 lines of configuration!
 * 
 * ============================================================================
 * 
 * SPRING BOOT:
 * 
 * @SpringBootApplication
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * 
 * That's it! Spring Boot:
 * ✅ Auto-configures DispatcherServlet
 * ✅ Auto-configures Jackson for JSON
 * ✅ Auto-configures static resource handling
 * ✅ Embeds Tomcat (no external server needed!)
 * ✅ Sets up sensible defaults for everything
 * 
 * Total: 7 lines!
 */

// ============================================================================
// VISUAL SUMMARY
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                    SPRING FRAMEWORK SETUP                               │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  ┌───────────────┐   ┌───────────────┐   ┌───────────────┐             │
 * │  │   web.xml     │   │applicationContext│  │ spring-mvc.xml│             │
 * │  │               │   │      .xml      │   │               │             │
 * │  │ - Dispatcher  │   │                │   │ - MVC config  │             │
 * │  │   Servlet     │   │ - DataSource   │   │ - ViewResolver│             │
 * │  │ - Listeners   │   │ - EntityMgr    │   │ - Jackson     │             │
 * │  │ - Filters     │   │ - TxManager    │   │ - Resources   │             │
 * │  │               │   │ - ComponentScan│   │               │             │
 * │  │  (50 lines)   │   │   (60 lines)   │   │  (30 lines)   │             │
 * │  └───────────────┘   └───────────────┘   └───────────────┘             │
 * │                                                                         │
 * │  + pom.xml with 15+ manually versioned dependencies                    │
 * │  + External Tomcat server installation                                 │
 * │  + WAR packaging and deployment                                        │
 * │                                                                         │
 * │  TOTAL: 140+ lines of configuration across 3-4 files                   │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                    SPRING BOOT SETUP                                    │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  ┌──────────────────────────┐   ┌────────────────────────┐             │
 * │  │  Application.java        │   │ application.properties │             │
 * │  │                          │   │                        │             │
 * │  │  @SpringBootApplication  │   │ server.port=8080       │             │
 * │  │  public class App {      │   │ spring.datasource.url= │             │
 * │  │    public static void    │   │ spring.jpa.show-sql=   │             │
 * │  │    main(String[] args) { │   │                        │             │
 * │  │      SpringApplication   │   │  (10 lines optional)   │             │
 * │  │        .run(App.class);  │   │                        │             │
 * │  │    }                     │   │                        │             │
 * │  │  }                       │   │                        │             │
 * │  │                          │   │                        │             │
 * │  │  (7 lines)               │   │                        │             │
 * │  └──────────────────────────┘   └────────────────────────┘             │
 * │                                                                         │
 * │  build.gradle with 3 starter dependencies (auto-versioned)             │
 * │  Embedded Tomcat (just run the JAR!)                                   │
 * │                                                                         │
 * │  TOTAL: 7-17 lines of configuration in 1-2 files                       │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

