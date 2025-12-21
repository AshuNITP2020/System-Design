package com.learning.java_interview;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                         SPRING DATA JPA - COMPLETE DEEP DIVE                                                   ║
 * ║                         (From JDBC → Hibernate → Spring Data JPA)                                              ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. EVOLUTION: WHY SPRING DATA JPA?                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   EVOLUTION TIMELINE:                                                                                          │
 * │   ──────────────────                                                                                           │
 * │                                                                                                                │
 * │   1997         2001           2006              2011              Present                                       │
 * │    │            │              │                 │                  │                                           │
 * │    ▼            ▼              ▼                 ▼                  ▼                                           │
 * │   JDBC  →   Hibernate   →    JPA 1.0    →   Spring Data JPA   →  Spring Data JPA                               │
 * │   (Raw)     (ORM)         (Standard)        (Repository)         + QueryDSL                                    │
 * │                                                                                                                │
 * │   Pain: Manual SQL     Pain: Still        Pain: Still           Solution:                                      │
 * │         ResultSet      verbose config     boilerplate DAO       Zero boilerplate!                              │
 * │         mapping                           implementation                                                       │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    2. JDBC (Plain JDBC) - THE PAINFUL ERA                                     ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   PROBLEMS WITH JDBC:                                                                                          │
 * │   ───────────────────                                                                                          │
 * │   1. Boilerplate code (Connection, Statement, ResultSet)                                                       │
 * │   2. Manual SQL writing for every operation                                                                    │
 * │   3. Manual ResultSet → Object mapping                                                                         │
 * │   4. Exception handling everywhere (SQLException)                                                              │
 * │   5. Resource management (close connections, statements)                                                       │
 * │   6. No caching, no lazy loading                                                                               │
 * │   7. Database-specific SQL                                                                                     │
 * │                                                                                                                │
 * │   EXAMPLE - Finding User by ID with JDBC:                                                                      │
 * │   ──────────────────────────────────────────                                                                   │
 * │                                                                                                                │
 * │   public User findById(Long id) {                                                                              │
 * │       Connection conn = null;                                                                                  │
 * │       PreparedStatement ps = null;                                                                             │
 * │       ResultSet rs = null;                                                                                     │
 * │       User user = null;                                                                                        │
 * │                                                                                                                │
 * │       try {                                                                                                    │
 * │           conn = DriverManager.getConnection(URL, USER, PASSWORD);                                             │
 * │           ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");                                      │
 * │           ps.setLong(1, id);                                                                                   │
 * │           rs = ps.executeQuery();                                                                              │
 * │                                                                                                                │
 * │           if (rs.next()) {                                                                                     │
 * │               user = new User();                                                                               │
 * │               user.setId(rs.getLong("id"));                                                                    │
 * │               user.setName(rs.getString("name"));                                                              │
 * │               user.setEmail(rs.getString("email"));                                                            │
 * │               // Map every column manually!                                                                    │
 * │           }                                                                                                    │
 * │       } catch (SQLException e) {                                                                               │
 * │           e.printStackTrace();  // Handle exception                                                            │
 * │       } finally {                                                                                              │
 * │           // Close everything in reverse order!                                                                │
 * │           try { if (rs != null) rs.close(); } catch (SQLException e) {}                                        │
 * │           try { if (ps != null) ps.close(); } catch (SQLException e) {}                                        │
 * │           try { if (conn != null) conn.close(); } catch (SQLException e) {}                                    │
 * │       }                                                                                                        │
 * │       return user;                                                                                             │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   LINES OF CODE: ~30+ for a simple findById! 😱                                                                │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    3. HIBERNATE / JPA - THE ORM ERA                                           ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   WHAT IS ORM?                                                                                                 │
 * │   ─────────────                                                                                                │
 * │   Object-Relational Mapping: Automatically maps Java objects ↔ Database tables                                 │
 * │                                                                                                                │
 * │   ┌────────────────────┐         ORM         ┌────────────────────┐                                            │
 * │   │   Java Object      │  ←─────────────────→│  Database Table    │                                            │
 * │   │                    │                     │                    │                                            │
 * │   │  class User {      │                     │  CREATE TABLE users│                                            │
 * │   │    Long id;        │ ←───────────────────│    id BIGINT,      │                                            │
 * │   │    String name;    │ ←───────────────────│    name VARCHAR,   │                                            │
 * │   │    String email;   │ ←───────────────────│    email VARCHAR   │                                            │
 * │   │  }                 │                     │  );                │                                            │
 * │   └────────────────────┘                     └────────────────────┘                                            │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   HIBERNATE vs JPA:                                                                                            │
 * │   ─────────────────                                                                                            │
 * │   • Hibernate (2001): First popular ORM framework for Java                                                     │
 * │   • JPA (2006): Java Persistence API - Standard specification                                                  │
 * │   • Hibernate implements JPA specification                                                                     │
 * │                                                                                                                │
 * │   ┌─────────────────────────────────────────────────────────────────────────────────────────────────┐          │
 * │   │                                                                                                 │          │
 * │   │                    JPA (Specification/Interface)                                                │          │
 * │   │                              │                                                                  │          │
 * │   │          ┌───────────────────┼───────────────────┐                                              │          │
 * │   │          │                   │                   │                                              │          │
 * │   │          ▼                   ▼                   ▼                                              │          │
 * │   │     Hibernate           EclipseLink         OpenJPA                                             │          │
 * │   │   (Most Popular)        (Reference)         (Apache)                                            │          │
 * │   │                                                                                                 │          │
 * │   └─────────────────────────────────────────────────────────────────────────────────────────────────┘          │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   EXAMPLE - Using Pure JPA/Hibernate (Still Verbose!):                                                         │
 * │   ────────────────────────────────────────────────────                                                         │
 * │                                                                                                                │
 * │   // Entity Class                                                                                              │
 * │   @Entity                                                                                                      │
 * │   @Table(name = "users")                                                                                       │
 * │   public class User {                                                                                          │
 * │       @Id                                                                                                      │
 * │       @GeneratedValue(strategy = GenerationType.IDENTITY)                                                      │
 * │       private Long id;                                                                                         │
 * │                                                                                                                │
 * │       @Column(name = "name")                                                                                   │
 * │       private String name;                                                                                     │
 * │                                                                                                                │
 * │       @Column(name = "email", unique = true)                                                                   │
 * │       private String email;                                                                                    │
 * │       // getters, setters                                                                                      │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   // DAO Implementation (Still needs manual implementation!)                                                   │
 * │   @Repository                                                                                                  │
 * │   public class UserDaoImpl implements UserDao {                                                                │
 * │                                                                                                                │
 * │       @PersistenceContext                                                                                      │
 * │       private EntityManager em;                                                                                │
 * │                                                                                                                │
 * │       @Override                                                                                                │
 * │       public User findById(Long id) {                                                                          │
 * │           return em.find(User.class, id);                                                                      │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       @Override                                                                                                │
 * │       public List<User> findAll() {                                                                            │
 * │           return em.createQuery("SELECT u FROM User u", User.class)                                            │
 * │                    .getResultList();                                                                           │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       @Override                                                                                                │
 * │       public User save(User user) {                                                                            │
 * │           if (user.getId() == null) {                                                                          │
 * │               em.persist(user);                                                                                │
 * │               return user;                                                                                     │
 * │           } else {                                                                                             │
 * │               return em.merge(user);                                                                           │
 * │           }                                                                                                    │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       @Override                                                                                                │
 * │       public void delete(User user) {                                                                          │
 * │           em.remove(em.contains(user) ? user : em.merge(user));                                                │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       @Override                                                                                                │
 * │       public List<User> findByEmail(String email) {                                                            │
 * │           return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)                     │
 * │                    .setParameter("email", email)                                                               │
 * │                    .getResultList();                                                                           │
 * │       }                                                                                                        │
 * │       // Repeat for EVERY entity... 😓                                                                         │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   PROBLEM: Same boilerplate DAO code for every entity!                                                         │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    4. SPRING DATA JPA - THE SOLUTION! 🎉                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   WHAT IS SPRING DATA JPA?                                                                                     │
 * │   ─────────────────────────                                                                                    │
 * │   • Abstraction layer on top of JPA                                                                            │
 * │   • Eliminates boilerplate DAO code                                                                            │
 * │   • Just define interface → Spring provides implementation!                                                    │
 * │   • Powerful query derivation from method names                                                                │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   SAME FUNCTIONALITY - ZERO BOILERPLATE:                                                                       │
 * │   ──────────────────────────────────────                                                                       │
 * │                                                                                                                │
 * │   // Entity (same as before)                                                                                   │
 * │   @Entity                                                                                                      │
 * │   public class User {                                                                                          │
 * │       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)                                                  │
 * │       private Long id;                                                                                         │
 * │       private String name;                                                                                     │
 * │       private String email;                                                                                    │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   // Repository - JUST AN INTERFACE! 🎉                                                                        │
 * │   public interface UserRepository extends JpaRepository<User, Long> {                                          │
 * │       // That's it! findById, findAll, save, delete - ALL PROVIDED!                                            │
 * │                                                                                                                │
 * │       // Custom queries - just define method name!                                                             │
 * │       List<User> findByEmail(String email);                                                                    │
 * │       List<User> findByNameContaining(String name);                                                            │
 * │       List<User> findByNameAndEmail(String name, String email);                                                │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   LINES OF CODE: ~5 vs 50+ with pure JPA! 😍                                                                   │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. REPOSITORY HIERARCHY                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │                            Repository<T, ID>  (Marker interface)                                               │
 * │                                    │                                                                           │
 * │                                    ▼                                                                           │
 * │                         CrudRepository<T, ID>                                                                  │
 * │                         ├─ save(entity)                                                                        │
 * │                         ├─ saveAll(entities)                                                                   │
 * │                         ├─ findById(id)                                                                        │
 * │                         ├─ findAll()                                                                           │
 * │                         ├─ findAllById(ids)                                                                    │
 * │                         ├─ count()                                                                             │
 * │                         ├─ deleteById(id)                                                                      │
 * │                         ├─ delete(entity)                                                                      │
 * │                         ├─ deleteAll()                                                                         │
 * │                         └─ existsById(id)                                                                      │
 * │                                    │                                                                           │
 * │                                    ▼                                                                           │
 * │                      PagingAndSortingRepository<T, ID>                                                         │
 * │                         ├─ findAll(Sort sort)                                                                  │
 * │                         └─ findAll(Pageable pageable)                                                          │
 * │                                    │                                                                           │
 * │                                    ▼                                                                           │
 * │                          JpaRepository<T, ID>  ← Most commonly used!                                           │
 * │                         ├─ flush()                                                                             │
 * │                         ├─ saveAndFlush(entity)                                                                │
 * │                         ├─ deleteInBatch(entities)                                                             │
 * │                         ├─ deleteAllInBatch()                                                                  │
 * │                         ├─ getOne(id) / getById(id)                                                            │
 * │                         └─ findAll(Example<S> example)                                                         │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    6. QUERY DERIVATION (Method Name Magic!) ✨                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   Spring Data JPA generates queries from method names!                                                         │
 * │                                                                                                                │
 * │   KEYWORDS AND EXAMPLES:                                                                                       │
 * │   ──────────────────────                                                                                       │
 * │                                                                                                                │
 * │   ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │   │ Method Name                                    │ Generated SQL (JPQL)                                     │
 * │   ├────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │   │ findByName(String name)                        │ WHERE name = ?1                                          │
 * │   │ findByNameAndEmail(String n, String e)         │ WHERE name = ?1 AND email = ?2                           │
 * │   │ findByNameOrEmail(String n, String e)          │ WHERE name = ?1 OR email = ?2                            │
 * │   │ findByAgeBetween(int min, int max)             │ WHERE age BETWEEN ?1 AND ?2                              │
 * │   │ findByAgeLessThan(int age)                     │ WHERE age < ?1                                           │
 * │   │ findByAgeLessThanEqual(int age)                │ WHERE age <= ?1                                          │
 * │   │ findByAgeGreaterThan(int age)                  │ WHERE age > ?1                                           │
 * │   │ findByAgeGreaterThanEqual(int age)             │ WHERE age >= ?1                                          │
 * │   │ findByNameLike(String pattern)                 │ WHERE name LIKE ?1                                       │
 * │   │ findByNameContaining(String s)                 │ WHERE name LIKE %?1%                                     │
 * │   │ findByNameStartingWith(String s)               │ WHERE name LIKE ?1%                                      │
 * │   │ findByNameEndingWith(String s)                 │ WHERE name LIKE %?1                                      │
 * │   │ findByNameIgnoreCase(String name)              │ WHERE UPPER(name) = UPPER(?1)                            │
 * │   │ findByAgeIn(Collection<Integer> ages)          │ WHERE age IN (?1)                                        │
 * │   │ findByAgeNotIn(Collection<Integer> ages)       │ WHERE age NOT IN (?1)                                    │
 * │   │ findByActiveTrue()                             │ WHERE active = true                                      │
 * │   │ findByActiveFalse()                            │ WHERE active = false                                     │
 * │   │ findByEmailIsNull()                            │ WHERE email IS NULL                                      │
 * │   │ findByEmailIsNotNull()                         │ WHERE email IS NOT NULL                                  │
 * │   │ findByNameOrderByAgeDesc(String name)          │ WHERE name = ?1 ORDER BY age DESC                        │
 * │   │ findTop5ByOrderByAgeDesc()                     │ ... ORDER BY age DESC LIMIT 5                            │
 * │   │ findFirst10ByName(String name)                 │ WHERE name = ?1 LIMIT 10                                 │
 * │   │ countByName(String name)                       │ SELECT COUNT(*) WHERE name = ?1                          │
 * │   │ deleteByName(String name)                      │ DELETE WHERE name = ?1                                   │
 * │   │ existsByEmail(String email)                    │ SELECT CASE WHEN EXISTS(...) THEN true ELSE false        │
 * │   └────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 * │                                                                                                                │
 * │   NESTED PROPERTY ACCESS:                                                                                      │
 * │   ───────────────────────                                                                                      │
 * │   findByAddressCity(String city)        → user.address.city = ?1                                               │
 * │   findByDepartmentName(String name)     → user.department.name = ?1                                            │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    7. CUSTOM QUERIES (@Query)                                                 ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   When method names become too complex, use @Query:                                                            │
 * │                                                                                                                │
 * │   // JPQL (Java Persistence Query Language) - Object-oriented                                                  │
 * │   @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")                                                   │
 * │   List<User> findByEmailDomain(@Param("domain") String domain);                                                │
 * │                                                                                                                │
 * │   // Native SQL (database-specific)                                                                            │
 * │   @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)                           │
 * │   List<User> findRecentUsers(@Param("date") LocalDateTime date);                                               │
 * │                                                                                                                │
 * │   // Positional parameters (index-based)                                                                       │
 * │   @Query("SELECT u FROM User u WHERE u.name = ?1 AND u.age > ?2")                                              │
 * │   List<User> findByNameAndMinAge(String name, int minAge);                                                     │
 * │                                                                                                                │
 * │   // Named parameters (recommended - more readable)                                                            │
 * │   @Query("SELECT u FROM User u WHERE u.name = :name AND u.age > :minAge")                                      │
 * │   List<User> findByNameAndMinAge(@Param("name") String name, @Param("minAge") int minAge);                     │
 * │                                                                                                                │
 * │   // UPDATE/DELETE with @Modifying                                                                             │
 * │   @Modifying                                                                                                   │
 * │   @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")                                       │
 * │   int deactivateInactiveUsers(@Param("date") LocalDateTime date);                                              │
 * │                                                                                                                │
 * │   @Modifying                                                                                                   │
 * │   @Query("DELETE FROM User u WHERE u.active = false")                                                          │
 * │   void deleteInactiveUsers();                                                                                  │
 * │                                                                                                                │
 * │   // Projection - Select specific fields                                                                       │
 * │   @Query("SELECT u.name, u.email FROM User u")                                                                 │
 * │   List<Object[]> findAllNamesAndEmails();                                                                      │
 * │                                                                                                                │
 * │   // DTO Projection (cleaner)                                                                                  │
 * │   @Query("SELECT new com.example.dto.UserDTO(u.name, u.email) FROM User u")                                    │
 * │   List<UserDTO> findAllAsDTO();                                                                                │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    8. PAGINATION AND SORTING                                                  ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   PAGINATION:                                                                                                  │
 * │   ───────────                                                                                                  │
 * │                                                                                                                │
 * │   // Repository                                                                                                │
 * │   Page<User> findByActive(boolean active, Pageable pageable);                                                  │
 * │                                                                                                                │
 * │   // Service                                                                                                   │
 * │   Pageable pageable = PageRequest.of(0, 10);  // Page 0, 10 items per page                                     │
 * │   Page<User> page = userRepository.findByActive(true, pageable);                                               │
 * │                                                                                                                │
 * │   // Page object contains:                                                                                     │
 * │   page.getContent();          // List<User> - actual data                                                      │
 * │   page.getTotalElements();    // Total records in DB                                                           │
 * │   page.getTotalPages();       // Total pages                                                                   │
 * │   page.getNumber();           // Current page number (0-based)                                                 │
 * │   page.getSize();             // Page size                                                                     │
 * │   page.hasNext();             // Has next page?                                                                │
 * │   page.hasPrevious();         // Has previous page?                                                            │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   SORTING:                                                                                                     │
 * │   ────────                                                                                                     │
 * │                                                                                                                │
 * │   // Simple sort                                                                                               │
 * │   Sort sort = Sort.by("name");                                                                                 │
 * │   Sort sortDesc = Sort.by("name").descending();                                                                │
 * │                                                                                                                │
 * │   // Multiple fields                                                                                           │
 * │   Sort multiSort = Sort.by("name").ascending()                                                                 │
 * │                         .and(Sort.by("age").descending());                                                     │
 * │                                                                                                                │
 * │   // With pagination                                                                                           │
 * │   Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());                                     │
 * │                                                                                                                │
 * │   // In repository method                                                                                      │
 * │   List<User> findByActiveTrue(Sort sort);                                                                      │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   SLICE vs PAGE:                                                                                               │
 * │   ───────────────                                                                                              │
 * │   • Page<T>: Runs COUNT query to get total → More overhead                                                     │
 * │   • Slice<T>: Only knows if hasNext → More efficient for infinite scroll                                       │
 * │                                                                                                                │
 * │   Slice<User> findByActive(boolean active, Pageable pageable);                                                 │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    9. ENTITY RELATIONSHIPS & ANNOTATIONS                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   RELATIONSHIP TYPES:                                                                                          │
 * │   ───────────────────                                                                                          │
 * │                                                                                                                │
 * │   @OneToOne                                                                                                    │
 * │   ──────────                                                                                                   │
 * │   @Entity                                                                                                      │
 * │   public class User {                                                                                          │
 * │       @OneToOne(cascade = CascadeType.ALL)                                                                     │
 * │       @JoinColumn(name = "profile_id", referencedColumnName = "id")                                            │
 * │       private Profile profile;                                                                                 │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   @OneToMany / @ManyToOne                                                                                      │
 * │   ────────────────────────                                                                                     │
 * │   @Entity                                                                                                      │
 * │   public class Department {                                                                                    │
 * │       @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)                     │
 * │       private List<Employee> employees = new ArrayList<>();                                                    │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   @Entity                                                                                                      │
 * │   public class Employee {                                                                                      │
 * │       @ManyToOne(fetch = FetchType.LAZY)                                                                       │
 * │       @JoinColumn(name = "department_id")                                                                      │
 * │       private Department department;                                                                           │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   @ManyToMany                                                                                                  │
 * │   ────────────                                                                                                 │
 * │   @Entity                                                                                                      │
 * │   public class Student {                                                                                       │
 * │       @ManyToMany                                                                                              │
 * │       @JoinTable(                                                                                              │
 * │           name = "student_course",                                                                             │
 * │           joinColumns = @JoinColumn(name = "student_id"),                                                      │
 * │           inverseJoinColumns = @JoinColumn(name = "course_id")                                                 │
 * │       )                                                                                                        │
 * │       private Set<Course> courses = new HashSet<>();                                                           │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   FETCH TYPES:                                                                                                 │
 * │   ────────────                                                                                                 │
 * │   • FetchType.LAZY (default for @OneToMany, @ManyToMany)                                                       │
 * │     → Loads data only when accessed                                                                            │
 * │     → Better performance, but watch for LazyInitializationException!                                           │
 * │                                                                                                                │
 * │   • FetchType.EAGER (default for @OneToOne, @ManyToOne)                                                        │
 * │     → Loads data immediately with parent                                                                       │
 * │     → Can cause performance issues with large datasets                                                         │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   CASCADE TYPES:                                                                                               │
 * │   ──────────────                                                                                               │
 * │   • CascadeType.PERSIST  → Save child when parent is saved                                                     │
 * │   • CascadeType.MERGE    → Update child when parent is updated                                                 │
 * │   • CascadeType.REMOVE   → Delete child when parent is deleted                                                 │
 * │   • CascadeType.REFRESH  → Refresh child when parent is refreshed                                              │
 * │   • CascadeType.DETACH   → Detach child when parent is detached                                                │
 * │   • CascadeType.ALL      → All of the above                                                                    │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    10. N+1 PROBLEM AND SOLUTIONS ⚠️                                           ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   WHAT IS N+1 PROBLEM?                                                                                         │
 * │   ─────────────────────                                                                                        │
 * │   Fetching N entities results in N+1 database queries!                                                         │
 * │                                                                                                                │
 * │   EXAMPLE:                                                                                                     │
 * │   List<Department> departments = departmentRepository.findAll();  // 1 query                                   │
 * │   for (Department dept : departments) {                                                                        │
 * │       dept.getEmployees().size();  // N queries (1 per department)!                                            │
 * │   }                                                                                                            │
 * │   // Total: 1 + N queries 😱                                                                                   │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   SOLUTIONS:                                                                                                   │
 * │   ──────────                                                                                                   │
 * │                                                                                                                │
 * │   1. JOIN FETCH (JPQL):                                                                                        │
 * │   @Query("SELECT d FROM Department d JOIN FETCH d.employees")                                                  │
 * │   List<Department> findAllWithEmployees();                                                                     │
 * │   // Single query with JOIN!                                                                                   │
 * │                                                                                                                │
 * │   2. @EntityGraph:                                                                                             │
 * │   @EntityGraph(attributePaths = {"employees"})                                                                 │
 * │   List<Department> findAll();                                                                                  │
 * │                                                                                                                │
 * │   3. @BatchSize (Hibernate):                                                                                   │
 * │   @OneToMany(mappedBy = "department")                                                                          │
 * │   @BatchSize(size = 25)  // Fetch 25 collections at once                                                       │
 * │   private List<Employee> employees;                                                                            │
 * │                                                                                                                │
 * │   4. @Fetch(FetchMode.SUBSELECT):                                                                              │
 * │   @OneToMany(mappedBy = "department")                                                                          │
 * │   @Fetch(FetchMode.SUBSELECT)                                                                                  │
 * │   private List<Employee> employees;                                                                            │
 * │   // Uses subquery to fetch all collections                                                                    │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    11. TRANSACTIONS (@Transactional)                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   @Transactional BASICS:                                                                                       │
 * │   ──────────────────────                                                                                       │
 * │                                                                                                                │
 * │   @Service                                                                                                     │
 * │   public class UserService {                                                                                   │
 * │                                                                                                                │
 * │       @Transactional  // Starts transaction, commits on success, rollbacks on exception                        │
 * │       public void transferMoney(Long fromId, Long toId, BigDecimal amount) {                                   │
 * │           User from = userRepository.findById(fromId).orElseThrow();                                           │
 * │           User to = userRepository.findById(toId).orElseThrow();                                               │
 * │                                                                                                                │
 * │           from.setBalance(from.getBalance().subtract(amount));                                                 │
 * │           to.setBalance(to.getBalance().add(amount));                                                          │
 * │                                                                                                                │
 * │           userRepository.save(from);                                                                           │
 * │           userRepository.save(to);                                                                             │
 * │           // If any exception → entire transaction rolls back                                                  │
 * │       }                                                                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   @Transactional ATTRIBUTES:                                                                                   │
 * │   ──────────────────────────                                                                                   │
 * │                                                                                                                │
 * │   @Transactional(                                                                                              │
 * │       readOnly = true,                    // Optimization hint for SELECT-only                                 │
 * │       timeout = 30,                       // Seconds before timeout                                            │
 * │       rollbackFor = Exception.class,      // Rollback on this exception                                        │
 * │       noRollbackFor = EmailException.class, // Don't rollback on this                                          │
 * │       propagation = Propagation.REQUIRED,  // Transaction propagation                                          │
 * │       isolation = Isolation.READ_COMMITTED // Isolation level                                                  │
 * │   )                                                                                                            │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   PROPAGATION TYPES:                                                                                           │
 * │   ──────────────────                                                                                           │
 * │   • REQUIRED (default): Use existing tx, or create new                                                         │
 * │   • REQUIRES_NEW: Always create new tx, suspend existing                                                       │
 * │   • SUPPORTS: Use existing tx if available, else non-tx                                                        │
 * │   • NOT_SUPPORTED: Execute non-tx, suspend existing                                                            │
 * │   • MANDATORY: Must have existing tx, else exception                                                           │
 * │   • NEVER: Must NOT have existing tx, else exception                                                           │
 * │   • NESTED: Nested tx with savepoints (JDBC only)                                                              │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   COMMON PITFALL - Self-invocation:                                                                            │
 * │   ─────────────────────────────────                                                                            │
 * │   @Service                                                                                                     │
 * │   public class UserService {                                                                                   │
 * │       public void doSomething() {                                                                              │
 * │           this.doTransactional();  // ❌ @Transactional IGNORED! (self-call)                                   │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       @Transactional                                                                                           │
 * │       public void doTransactional() { ... }                                                                    │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   Solution: Inject self or use ApplicationContext to get proxied bean                                          │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    12. AUDITING                                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   Automatically track created/modified timestamps and users:                                                   │
 * │                                                                                                                │
 * │   1. Enable auditing:                                                                                          │
 * │   @Configuration                                                                                               │
 * │   @EnableJpaAuditing                                                                                           │
 * │   public class JpaConfig { }                                                                                   │
 * │                                                                                                                │
 * │   2. Add audit fields to entity:                                                                               │
 * │   @Entity                                                                                                      │
 * │   @EntityListeners(AuditingEntityListener.class)                                                               │
 * │   public class User {                                                                                          │
 * │       @Id @GeneratedValue                                                                                      │
 * │       private Long id;                                                                                         │
 * │                                                                                                                │
 * │       @CreatedDate                                                                                             │
 * │       @Column(updatable = false)                                                                               │
 * │       private LocalDateTime createdAt;                                                                         │
 * │                                                                                                                │
 * │       @LastModifiedDate                                                                                        │
 * │       private LocalDateTime updatedAt;                                                                         │
 * │                                                                                                                │
 * │       @CreatedBy                                                                                               │
 * │       @Column(updatable = false)                                                                               │
 * │       private String createdBy;                                                                                │
 * │                                                                                                                │
 * │       @LastModifiedBy                                                                                          │
 * │       private String modifiedBy;                                                                               │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   3. For @CreatedBy/@LastModifiedBy, implement AuditorAware:                                                   │
 * │   @Component                                                                                                   │
 * │   public class AuditorAwareImpl implements AuditorAware<String> {                                              │
 * │       @Override                                                                                                │
 * │       public Optional<String> getCurrentAuditor() {                                                            │
 * │           return Optional.of(SecurityContextHolder.getContext()                                                │
 * │               .getAuthentication().getName());                                                                 │
 * │       }                                                                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    13. SPECIFICATIONS (Dynamic Queries)                                       ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   For dynamic/complex queries, use JPA Specifications:                                                         │
 * │                                                                                                                │
 * │   // Repository extends JpaSpecificationExecutor                                                               │
 * │   public interface UserRepository extends JpaRepository<User, Long>,                                           │
 * │                                           JpaSpecificationExecutor<User> { }                                   │
 * │                                                                                                                │
 * │   // Specification class                                                                                       │
 * │   public class UserSpecifications {                                                                            │
 * │                                                                                                                │
 * │       public static Specification<User> hasName(String name) {                                                 │
 * │           return (root, query, cb) ->                                                                          │
 * │               name == null ? null : cb.equal(root.get("name"), name);                                          │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       public static Specification<User> hasAgeGreaterThan(Integer age) {                                       │
 * │           return (root, query, cb) ->                                                                          │
 * │               age == null ? null : cb.greaterThan(root.get("age"), age);                                       │
 * │       }                                                                                                        │
 * │                                                                                                                │
 * │       public static Specification<User> isActive() {                                                           │
 * │           return (root, query, cb) -> cb.isTrue(root.get("active"));                                           │
 * │       }                                                                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   // Usage - Combine specifications                                                                            │
 * │   Specification<User> spec = Specification                                                                     │
 * │       .where(UserSpecifications.hasName("John"))                                                               │
 * │       .and(UserSpecifications.hasAgeGreaterThan(18))                                                           │
 * │       .and(UserSpecifications.isActive());                                                                     │
 * │                                                                                                                │
 * │   List<User> users = userRepository.findAll(spec);                                                             │
 * │   Page<User> pagedUsers = userRepository.findAll(spec, pageable);                                              │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    14. PROJECTIONS (DTOs & Interfaces)                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   Fetch only required fields (better performance than fetching entire entity):                                 │
 * │                                                                                                                │
 * │   1. INTERFACE-BASED PROJECTION (Closed):                                                                      │
 * │   ────────────────────────────────────────                                                                     │
 * │   public interface UserNameOnly {                                                                              │
 * │       String getName();                                                                                        │
 * │       String getEmail();                                                                                       │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   // Repository                                                                                                │
 * │   List<UserNameOnly> findByActiveTrue();                                                                       │
 * │   // Spring creates proxy implementing interface!                                                              │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   2. CLASS-BASED PROJECTION (DTO):                                                                             │
 * │   ─────────────────────────────────                                                                            │
 * │   public class UserDTO {                                                                                       │
 * │       private String name;                                                                                     │
 * │       private String email;                                                                                    │
 * │                                                                                                                │
 * │       public UserDTO(String name, String email) {  // Constructor must match!                                  │
 * │           this.name = name;                                                                                    │
 * │           this.email = email;                                                                                  │
 * │       }                                                                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │   // Repository with @Query                                                                                    │
 * │   @Query("SELECT new com.example.dto.UserDTO(u.name, u.email) FROM User u")                                    │
 * │   List<UserDTO> findAllAsDTO();                                                                                │
 * │                                                                                                                │
 * │                                                                                                                │
 * │   3. DYNAMIC PROJECTION:                                                                                       │
 * │   ───────────────────────                                                                                      │
 * │   <T> List<T> findByActiveTrue(Class<T> type);                                                                 │
 * │                                                                                                                │
 * │   // Usage                                                                                                     │
 * │   List<UserNameOnly> names = repo.findByActiveTrue(UserNameOnly.class);                                        │
 * │   List<User> fullUsers = repo.findByActiveTrue(User.class);                                                    │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    15. COMPLETE COMPARISON                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                               │
 * │   Feature              │  JDBC           │  Hibernate/JPA   │  Spring Data JPA                               │
 * │   ─────────────────────┼─────────────────┼──────────────────┼────────────────────────────────────────────────│
 * │   SQL Writing          │  Manual         │  JPQL/HQL        │  Method names / @Query                         │
 * │   Object Mapping       │  Manual         │  Automatic       │  Automatic                                     │
 * │   Connection Mgmt      │  Manual         │  Automatic       │  Automatic                                     │
 * │   Transaction Mgmt     │  Manual         │  @Transactional  │  @Transactional                                │
 * │   Caching              │  None           │  L1 + L2         │  L1 + L2 (Hibernate)                           │
 * │   Lazy Loading         │  No             │  Yes             │  Yes                                           │
 * │   Repository Code      │  ~100 lines     │  ~50 lines       │  ~5 lines! 🎉                                  │
 * │   Learning Curve       │  Low            │  Medium          │  Low (if know JPA)                             │
 * │   Flexibility          │  High           │  High            │  Medium (can use @Query)                       │
 * │   Boilerplate          │  High           │  Medium          │  Almost Zero                                   │
 * │                                                                                                               │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    16. INTERVIEW QUESTIONS                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: What is the difference between JPA and Hibernate?
 * A: JPA is a specification (interface), Hibernate is an implementation.
 *    JPA defines what to do, Hibernate defines how to do it.
 *    Other implementations: EclipseLink, OpenJPA.
 *
 * Q2: What is the difference between save() and saveAndFlush()?
 * A: save() - Persists entity but doesn't immediately sync to DB (batched).
 *    saveAndFlush() - Persists AND immediately syncs to DB.
 *    Use saveAndFlush when you need the generated ID immediately.
 *
 * Q3: What is the N+1 problem and how do you solve it?
 * A: Loading N entities causes N+1 queries (1 for parent, N for children).
 *    Solutions: JOIN FETCH, @EntityGraph, @BatchSize, FetchMode.SUBSELECT.
 *
 * Q4: Difference between findById() and getById()/getOne()?
 * A: findById() - Returns Optional, executes SELECT immediately.
 *    getById() - Returns proxy, SELECT only when property accessed (lazy).
 *    Use findById() when you need the entity immediately.
 *
 * Q5: What is the difference between EAGER and LAZY fetching?
 * A: EAGER - Loads related entities immediately with parent query.
 *    LAZY - Loads related entities only when accessed.
 *    LAZY is better for performance but watch for LazyInitializationException.
 *
 * Q6: What is @Modifying annotation used for?
 * A: Required for UPDATE/DELETE @Query methods.
 *    Tells Spring the query modifies data.
 *    Usually combined with @Transactional.
 *
 * Q7: What is orphanRemoval in @OneToMany?
 * A: If true, child entities removed from collection are deleted from DB.
 *    Different from CascadeType.REMOVE which deletes children when parent deleted.
 *    orphanRemoval deletes when child removed from parent's collection.
 *
 * Q8: How does Spring Data JPA create repository implementations?
 * A: Uses JDK Dynamic Proxy at runtime.
 *    Parses method names to generate queries.
 *    Creates implementation class that extends SimpleJpaRepository.
 *
 * Q9: What is @EntityGraph and when to use it?
 * A: Defines which associations to fetch eagerly for a specific query.
 *    Alternative to JPQL JOIN FETCH.
 *    Avoids N+1 without changing entity default fetch type.
 *
 * Q10: How to handle transactions spanning multiple repositories?
 * A: Use @Transactional at Service layer (not Repository).
 *    All repository calls within the method share same transaction.
 *    Rollback if any exception occurs.
 *
 *
 * @author Java Interview Guide
 */
public class _11_SpringDataJPA {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                         SPRING DATA JPA - DEEP DIVE");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        System.out.println("This file is a comprehensive documentation of Spring Data JPA concepts.");
        System.out.println("Review the Javadoc comments above for detailed explanations.\n");

        System.out.println("KEY TOPICS COVERED:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");
        System.out.println("  1.  Evolution: JDBC → Hibernate/JPA → Spring Data JPA");
        System.out.println("  2.  JDBC Pain Points (Manual SQL, ResultSet mapping)");
        System.out.println("  3.  Hibernate/JPA (ORM, Entity mapping)");
        System.out.println("  4.  Spring Data JPA (Zero boilerplate repositories)");
        System.out.println("  5.  Repository Hierarchy (Repository → CrudRepository → JpaRepository)");
        System.out.println("  6.  Query Derivation (Method name magic!)");
        System.out.println("  7.  Custom Queries (@Query, JPQL, Native SQL)");
        System.out.println("  8.  Pagination and Sorting (Page, Slice, Sort)");
        System.out.println("  9.  Entity Relationships (@OneToMany, @ManyToOne, etc.)");
        System.out.println("  10. N+1 Problem and Solutions (JOIN FETCH, @EntityGraph)");
        System.out.println("  11. Transactions (@Transactional, Propagation)");
        System.out.println("  12. Auditing (@CreatedDate, @LastModifiedBy)");
        System.out.println("  13. Specifications (Dynamic Queries)");
        System.out.println("  14. Projections (DTOs, Interface projections)");
        System.out.println("  15. Complete Comparison Table");
        System.out.println("  16. Interview Questions");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────\n");

        // Quick code examples
        demonstrateRepositoryExample();
    }

    static void demonstrateRepositoryExample() {
        System.out.println("▶ QUICK CODE COMPARISON:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────\n");

        System.out.println("   JDBC (30+ lines for findById):");
        System.out.println("   ─────────────────────────────────────────");
        System.out.println("   Connection conn = DriverManager.getConnection(...);");
        System.out.println("   PreparedStatement ps = conn.prepareStatement(\"SELECT * FROM users WHERE id=?\");");
        System.out.println("   ps.setLong(1, id);");
        System.out.println("   ResultSet rs = ps.executeQuery();");
        System.out.println("   // Manual mapping, exception handling, resource cleanup...\n");

        System.out.println("   JPA/Hibernate (15+ lines):");
        System.out.println("   ─────────────────────────────────────────");
        System.out.println("   @PersistenceContext");
        System.out.println("   EntityManager em;");
        System.out.println("   public User findById(Long id) {");
        System.out.println("       return em.find(User.class, id);");
        System.out.println("   }\n");

        System.out.println("   Spring Data JPA (3 lines!) 🎉:");
        System.out.println("   ─────────────────────────────────────────");
        System.out.println("   public interface UserRepository extends JpaRepository<User, Long> {");
        System.out.println("       // findById() is already provided!");
        System.out.println("   }\n");

        System.out.println("   Custom query with method name:");
        System.out.println("   ─────────────────────────────────────────");
        System.out.println("   List<User> findByNameContainingAndAgeGreaterThanOrderByNameAsc(String name, int age);");
        System.out.println("   // Generates: SELECT * FROM users WHERE name LIKE %?% AND age > ? ORDER BY name ASC\n");
    }
}