// ============================================================================
// Q1: BEAN SCOPES - USE CASES
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  SINGLETON (Default) - One Instance for Entire Application             │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  USE CASES:                                                             │
 * │  • Stateless services (PaymentService, OrderService)                   │
 * │  • Database connection pools (DataSource)                              │
 * │  • Configuration holders (AppConfig)                                   │
 * │  • Caches (CacheService)                                               │
 * │  • Utility services (EmailService, LoggingService)                     │
 * │                                                                         │
 * │  EXAMPLE:                                                               │
 * │  @Service                                                               │
 * │  class PaymentService {                                                 │
 * │      public void processPayment(Order order) {                         │
 * │          // No instance state - safe to share                          │
 * │      }                                                                  │
 * │  }                                                                      │
 * │                                                                         │
 * │  WHEN TO USE: 99% of your beans! Default choice.                       │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  PROTOTYPE - New Instance Every Time                                    │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  USE CASES:                                                             │
 * │  • Report generators (accumulate data during generation)               │
 * │  • Email builders (stateful builder pattern)                           │
 * │  • Non-thread-safe third-party libraries                               │
 * │  • Objects that hold per-operation state                               │
 * │                                                                         │
 * │  EXAMPLE:                                                               │
 * │  @Component                                                             │
 * │  @Scope("prototype")                                                    │
 * │  class ReportGenerator {                                                │
 * │      private List<String> sections = new ArrayList<>();                │
 * │      public void addSection(String s) { sections.add(s); }             │
 * │      public Report generate() { return new Report(sections); }         │
 * │  }                                                                      │
 * │                                                                         │
 * │  // Each call gets NEW instance:                                        │
 * │  ReportGenerator gen1 = context.getBean(ReportGenerator.class);        │
 * │  ReportGenerator gen2 = context.getBean(ReportGenerator.class);        │
 * │  // gen1 != gen2 (different instances)                                 │
 * │                                                                         │
 * │  WHEN TO USE: Rare! Only for stateful, per-operation objects.          │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  REQUEST - One Instance Per HTTP Request                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  USE CASES:                                                             │
 * │  • Request context (userId, requestId, startTime)                      │
 * │  • Per-request audit logging                                           │
 * │  • Multi-tenant context (tenantId per request)                         │
 * │  • Request correlation IDs for tracing                                 │
 * │                                                                         │
 * │  EXAMPLE:                                                               │
 * │  @Component                                                             │
 * │  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)   │
 * │  class RequestContext {                                                 │
 * │      private String userId;                                             │
 * │      private String requestId = UUID.randomUUID().toString();          │
 * │      // Getters and setters                                             │
 * │  }                                                                      │
 * │                                                                         │
 * │  // Every service in the same request sees same RequestContext          │
 * │  // Different requests get different instances                          │
 * │                                                                         │
 * │  WHEN TO USE: Per-request tracking, multi-tenant apps.                 │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  SESSION - One Instance Per User Session                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  USE CASES:                                                             │
 * │  • Shopping carts (persists across page views)                         │
 * │  • User preferences during session                                     │
 * │  • Multi-step wizards/forms                                            │
 * │  • Recently viewed items                                                │
 * │                                                                         │
 * │  EXAMPLE:                                                               │
 * │  @Component                                                             │
 * │  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)   │
 * │  class ShoppingCart {                                                   │
 * │      private List<CartItem> items = new ArrayList<>();                 │
 * │                                                                         │
 * │      public void addItem(Product p) { items.add(new CartItem(p)); }    │
 * │      public List<CartItem> getItems() { return items; }                │
 * │  }                                                                      │
 * │                                                                         │
 * │  // User A's cart is separate from User B's cart                       │
 * │  // Same user's cart persists across page navigations                  │
 * │                                                                         │
 * │  WHEN TO USE: User-specific state that persists during session.        │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

/*
 * DECISION FLOWCHART:
 * 
 *     Does the bean hold state?
 *             │
 *             ├── NO → Use SINGLETON ✅ (99% of cases)
 *             │
 *             └── YES → Is the state per-user?
 *                             │
 *                             ├── YES → Should it persist across requests?
 *                             │              │
 *                             │              ├── YES → Use SESSION
 *                             │              │         (cart, preferences)
 *                             │              │
 *                             │              └── NO → Use REQUEST
 *                             │                       (request context)
 *                             │
 *                             └── NO → Does each operation need fresh state?
 *                                            │
 *                                            ├── YES → Use PROTOTYPE
 *                                            │         (builders, generators)
 *                                            │
 *                                            └── NO → Use SINGLETON
 *                                                     (with synchronization)
 * 
 * 
 * SUMMARY TABLE:
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Scope      │ Instances  │ Lifetime           │ Use Cases              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  singleton  │ ONE        │ Entire app         │ Services, configs      │
 * │  prototype  │ NEW each   │ Until GC           │ Builders, generators   │
 * │  request    │ ONE/request│ Single request     │ Request context        │
 * │  session    │ ONE/session│ Session expires    │ Shopping cart          │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// BEST PRACTICES SUMMARY
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                         BEST PRACTICES                                  │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  1. Use CONSTRUCTOR INJECTION for required dependencies                │
 * │     - Fields can be final                                              │
 * │     - Easy to test                                                     │
 * │     - Fails fast if dependency missing                                 │
 * │                                                                         │
 * │  2. Use SETTER INJECTION only for optional dependencies                │
 * │     - @Autowired(required = false)                                     │
 * │     - Check for null before using                                      │
 * │                                                                         │
 * │  3. AVOID FIELD INJECTION in production code                           │
 * │     - OK for test classes                                              │
 * │     - OK for quick prototypes                                          │
 * │                                                                         │
 * │  4. Use SINGLETON scope for 99% of beans                               │
 * │     - Default and most efficient                                       │
 * │                                                                         │
 * │  5. Use PROTOTYPE only when truly needed                               │
 * │     - Stateful per-operation objects                                   │
 * │                                                                         │
 * │  6. Use REQUEST/SESSION for web-specific state                         │
 * │     - Request context, shopping carts                                  │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

