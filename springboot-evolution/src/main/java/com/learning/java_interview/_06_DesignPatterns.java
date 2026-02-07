package com.learning.java_interview;

import java.util.*;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                                  DESIGN PATTERNS - INTERVIEW DEEP DIVE                                         ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    DESIGN PATTERNS CATEGORIES                                                  ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  CREATIONAL                      STRUCTURAL                     BEHAVIORAL                                    │
 *  │  (Object creation)               (Object composition)           (Object interaction)                          │
 *  │  ──────────────────              ────────────────────           ──────────────────────                         │
 *  │  • Singleton ⭐                  • Adapter ⭐                   • Strategy ⭐                                  │
 *  │  • Factory Method ⭐             • Decorator ⭐                 • Observer ⭐                                  │
 *  │  • Abstract Factory              • Proxy                        • Template Method                              │
 *  │  • Builder ⭐                    • Facade                       • Command                                      │
 *  │  • Prototype                     • Bridge                       • Iterator                                     │
 *  │                                  • Composite                    • State                                        │
 *  │                                  • Flyweight                    • Chain of Responsibility                      │
 *  │                                                                 • Mediator                                     │
 *  │                                                                 • Memento                                      │
 *  │                                                                 • Visitor                                      │
 *  │                                                                                                                │
 *  │  ⭐ = Most frequently asked in interviews                                                                     │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. SINGLETON PATTERN                                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: Ensure only ONE instance of a class exists throughout the application.
 *
 *  USE CASES:
 *  • Logger
 *  • Configuration manager
 *  • Database connection pool
 *  • Cache
 *
 *  WAYS TO BREAK SINGLETON:
 *  1. Reflection - use enum to prevent
 *  2. Serialization - implement readResolve()
 *  3. Cloning - throw exception in clone()
 *  4. Multiple classloaders
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    2. FACTORY PATTERN                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: Create objects without exposing instantiation logic. Client uses interface.
 *
 *  USE CASES:
 *  • Different database drivers
 *  • Shape creation (Circle, Rectangle)
 *  • Document creation (PDF, Word, Excel)
 *  • Payment processors
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    3. BUILDER PATTERN                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: Construct complex objects step by step. Separate construction from representation.
 *
 *  USE CASES:
 *  • Building complex objects with many optional parameters
 *  • StringBuilder
 *  • HTTP request builders
 *  • Query builders
 *
 *  BENEFITS:
 *  • More readable than telescoping constructors
 *  • Immutable objects can be built
 *  • Fluent API
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    4. STRATEGY PATTERN                                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: Define a family of algorithms, encapsulate each one, make them interchangeable.
 *
 *  USE CASES:
 *  • Payment methods (Credit Card, PayPal, UPI)
 *  • Sorting algorithms
 *  • Compression algorithms
 *  • Authentication strategies
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. OBSERVER PATTERN                                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: One-to-many dependency. When one object changes, all dependents are notified.
 *
 *  USE CASES:
 *  • Event handling systems
 *  • Newsletter subscriptions
 *  • Stock price updates
 *  • Model-View in MVC
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    6. DECORATOR PATTERN                                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: Add behavior to objects dynamically without modifying their structure.
 *
 *  USE CASES:
 *  • Java I/O streams (BufferedInputStream decorates FileInputStream)
 *  • Adding toppings to pizza
 *  • Adding features to UI components
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    7. ADAPTER PATTERN                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PURPOSE: Convert interface of a class into another interface clients expect.
 *
 *  USE CASES:
 *  • Legacy code integration
 *  • Third-party library integration
 *  • Arrays.asList() - adapts array to List
 *
 *
 * @author Java Interview Guide
 */
public class _06_DesignPatterns {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                        DESIGN PATTERNS DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. Singleton Pattern
        demonstrateSingleton();

        // 2. Factory Pattern
        demonstrateFactory();

        // 3. Builder Pattern
        demonstrateBuilder();

        // 4. Strategy Pattern
        demonstrateStrategy();

        // 5. Observer Pattern
        demonstrateObserver();

        // 6. Decorator Pattern
        demonstrateDecorator();

        // 7. Adapter Pattern
        demonstrateAdapter();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. SINGLETON PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateSingleton() {
        System.out.println("▶ 1. SINGLETON PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // All calls return same instance
        DatabaseConnection conn1 = DatabaseConnection.getInstance();
        DatabaseConnection conn2 = DatabaseConnection.getInstance();

        System.out.println("   conn1 == conn2: " + (conn1 == conn2));
        System.out.println("   conn1.hashCode(): " + conn1.hashCode());
        System.out.println("   conn2.hashCode(): " + conn2.hashCode());

        conn1.connect("mydb");

        // Enum Singleton (safest approach)
        Logger logger = Logger.INSTANCE;
        logger.log("This is a log message");

        System.out.println();
    }

    // Thread-safe Singleton using Double-Checked Locking
    static class DatabaseConnection {
        private static volatile DatabaseConnection instance;  // volatile for visibility

        private DatabaseConnection() {
            // Private constructor
            System.out.println("   DatabaseConnection instance created");
        }

        public static DatabaseConnection getInstance() {
            if (instance == null) {  // First check (no locking)
                synchronized (DatabaseConnection.class) {
                    if (instance == null) {  // Second check (with locking)
                        instance = new DatabaseConnection();
                    }
                }
            }
            return instance;
        }

        public void connect(String db) {
            System.out.println("   Connected to: " + db);
        }
    }

    // Enum Singleton (Best approach - thread-safe, serialization-safe, reflection-safe)
    enum Logger {
        INSTANCE;

        public void log(String message) {
            System.out.println("   [LOG] " + message);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. FACTORY PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateFactory() {
        System.out.println("▶ 2. FACTORY PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Client doesn't need to know concrete classes
        Shape circle = ShapeFactory.createShape("CIRCLE");
        Shape rectangle = ShapeFactory.createShape("RECTANGLE");

        circle.draw();
        rectangle.draw();

        System.out.println();
    }

    // Product interface
    interface Shape {
        void draw();
    }

    // Concrete products
    static class Circle implements Shape {
        @Override
        public void draw() {
            System.out.println("   Drawing Circle");
        }
    }

    static class Rectangle implements Shape {
        @Override
        public void draw() {
            System.out.println("   Drawing Rectangle");
        }
    }

    // Factory
    static class ShapeFactory {
        public static Shape createShape(String type) {
            return switch (type.toUpperCase()) {
                case "CIRCLE" -> new Circle();
                case "RECTANGLE" -> new Rectangle();
                default -> throw new IllegalArgumentException("Unknown shape: " + type);
            };
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. BUILDER PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateBuilder() {
        System.out.println("▶ 3. BUILDER PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Fluent API - easy to read
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .age(30)
                .phone("1234567890")  // Optional
                .build();

        System.out.println("   Created user: " + user);

        // Can create different configurations
        User minimalUser = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .build();

        System.out.println("   Minimal user: " + minimalUser);

        System.out.println();
    }

    // Builder Pattern Implementation
    static class User {
        private final String firstName;  // Required
        private final String lastName;   // Required
        private final String email;      // Required
        private final int age;           // Optional
        private final String phone;      // Optional

        private User(UserBuilder builder) {
            this.firstName = builder.firstName;
            this.lastName = builder.lastName;
            this.email = builder.email;
            this.age = builder.age;
            this.phone = builder.phone;
        }

        public static UserBuilder builder() {
            return new UserBuilder();
        }

        @Override
        public String toString() {
            return "User{name='" + firstName + " " + lastName + "', email='" + email +
                    "', age=" + age + ", phone='" + phone + "'}";
        }

        // Static inner Builder class
        static class UserBuilder {
            private String firstName;
            private String lastName;
            private String email;
            private int age;
            private String phone;

            public UserBuilder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public UserBuilder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public UserBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserBuilder age(int age) {
                this.age = age;
                return this;
            }

            public UserBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }

            public User build() {
                // Validation
                if (firstName == null || lastName == null || email == null) {
                    throw new IllegalStateException("firstName, lastName, and email are required");
                }
                return new User(this);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. STRATEGY PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateStrategy() {
        System.out.println("▶ 4. STRATEGY PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Laptop", 1000);
        cart.addItem("Mouse", 50);

        // Pay with Credit Card
        cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
        cart.checkout();

        // Pay with PayPal
        cart.setPaymentStrategy(new PayPalPayment("user@email.com"));
        cart.checkout();

        // Pay with UPI
        cart.setPaymentStrategy(new UPIPayment("user@upi"));
        cart.checkout();

        System.out.println();
    }

    // Strategy interface
    interface PaymentStrategy {
        void pay(double amount);
    }

    // Concrete strategies
    static class CreditCardPayment implements PaymentStrategy {
        private String cardNumber;

        public CreditCardPayment(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        @Override
        public void pay(double amount) {
            System.out.println("   Paid $" + amount + " using Credit Card: " + maskCard(cardNumber));
        }

        private String maskCard(String card) {
            return "****-****-****-" + card.substring(card.length() - 4);
        }
    }

    static class PayPalPayment implements PaymentStrategy {
        private String email;

        public PayPalPayment(String email) {
            this.email = email;
        }

        @Override
        public void pay(double amount) {
            System.out.println("   Paid $" + amount + " using PayPal: " + email);
        }
    }

    static class UPIPayment implements PaymentStrategy {
        private String upiId;

        public UPIPayment(String upiId) {
            this.upiId = upiId;
        }

        @Override
        public void pay(double amount) {
            System.out.println("   Paid $" + amount + " using UPI: " + upiId);
        }
    }

    // Context class
    static class ShoppingCart {
        private List<Item> items = new ArrayList<>();
        private PaymentStrategy paymentStrategy;

        public void addItem(String name, double price) {
            items.add(new Item(name, price));
        }

        public void setPaymentStrategy(PaymentStrategy strategy) {
            this.paymentStrategy = strategy;
        }

        public void checkout() {
            double total = items.stream().mapToDouble(i -> i.price).sum();
            paymentStrategy.pay(total);
        }

        record Item(String name, double price) {}
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. OBSERVER PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateObserver() {
        System.out.println("▶ 5. OBSERVER PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Subject (Publisher)
        StockMarket stockMarket = new StockMarket();

        // Observers (Subscribers)
        StockObserver investor1 = new StockObserver("Alice");
        StockObserver investor2 = new StockObserver("Bob");

        // Subscribe
        stockMarket.addObserver(investor1);
        stockMarket.addObserver(investor2);

        // Notify all observers
        stockMarket.setStockPrice("AAPL", 150.0);
        stockMarket.setStockPrice("GOOGL", 2800.0);

        // Unsubscribe one observer
        stockMarket.removeObserver(investor2);
        stockMarket.setStockPrice("AAPL", 155.0);

        System.out.println();
    }

    // Observer interface
    interface Observer {
        void update(String stock, double price);
    }

    // Subject interface
    interface Subject {
        void addObserver(Observer o);
        void removeObserver(Observer o);
        void notifyObservers();
    }

    // Concrete Subject
    static class StockMarket implements Subject {
        private List<Observer> observers = new ArrayList<>();
        private String currentStock;
        private double currentPrice;

        @Override
        public void addObserver(Observer o) {
            observers.add(o);
        }

        @Override
        public void removeObserver(Observer o) {
            observers.remove(o);
        }

        @Override
        public void notifyObservers() {
            for (Observer o : observers) {
                o.update(currentStock, currentPrice);
            }
        }

        public void setStockPrice(String stock, double price) {
            this.currentStock = stock;
            this.currentPrice = price;
            notifyObservers();
        }
    }

    // Concrete Observer
    static class StockObserver implements Observer {
        private String name;

        public StockObserver(String name) {
            this.name = name;
        }

        @Override
        public void update(String stock, double price) {
            System.out.println("   " + name + " notified: " + stock + " is now $" + price);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         6. DECORATOR PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateDecorator() {
        System.out.println("▶ 6. DECORATOR PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Base coffee
        Coffee coffee = new SimpleCoffee();
        System.out.println("   " + coffee.getDescription() + " : $" + coffee.getCost());

        // Add milk
        coffee = new MilkDecorator(coffee);
        System.out.println("   " + coffee.getDescription() + " : $" + coffee.getCost());

        // Add sugar
        coffee = new SugarDecorator(coffee);
        System.out.println("   " + coffee.getDescription() + " : $" + coffee.getCost());

        // Add whipped cream
        coffee = new WhippedCreamDecorator(coffee);
        System.out.println("   " + coffee.getDescription() + " : $" + coffee.getCost());

        System.out.println();
    }

    // Component interface
    interface Coffee {
        String getDescription();
        double getCost();
    }

    // Concrete component
    static class SimpleCoffee implements Coffee {
        @Override
        public String getDescription() {
            return "Simple Coffee";
        }

        @Override
        public double getCost() {
            return 2.0;
        }
    }

    // Base decorator
    static abstract class CoffeeDecorator implements Coffee {
        protected Coffee decoratedCoffee;

        public CoffeeDecorator(Coffee coffee) {
            this.decoratedCoffee = coffee;
        }

        @Override
        public String getDescription() {
            return decoratedCoffee.getDescription();
        }

        @Override
        public double getCost() {
            return decoratedCoffee.getCost();
        }
    }

    // Concrete decorators
    static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public String getDescription() {
            return decoratedCoffee.getDescription() + ", Milk";
        }

        @Override
        public double getCost() {
            return decoratedCoffee.getCost() + 0.5;
        }
    }

    static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public String getDescription() {
            return decoratedCoffee.getDescription() + ", Sugar";
        }

        @Override
        public double getCost() {
            return decoratedCoffee.getCost() + 0.2;
        }
    }

    static class WhippedCreamDecorator extends CoffeeDecorator {
        public WhippedCreamDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public String getDescription() {
            return decoratedCoffee.getDescription() + ", Whipped Cream";
        }

        @Override
        public double getCost() {
            return decoratedCoffee.getCost() + 0.7;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         7. ADAPTER PATTERN
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateAdapter() {
        System.out.println("▶ 7. ADAPTER PATTERN:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Client expects MediaPlayer interface
        MediaPlayer audioPlayer = new AudioPlayer();
        audioPlayer.play("mp3", "song.mp3");

        // Using adapter to play different formats
        MediaPlayer vlcAdapter = new MediaAdapter("vlc");
        vlcAdapter.play("vlc", "movie.vlc");

        MediaPlayer mp4Adapter = new MediaAdapter("mp4");
        mp4Adapter.play("mp4", "video.mp4");

        System.out.println();
    }

    // Target interface (what client expects)
    interface MediaPlayer {
        void play(String audioType, String fileName);
    }

    // Adaptee interface (existing interface)
    interface AdvancedMediaPlayer {
        void playVlc(String fileName);
        void playMp4(String fileName);
    }

    // Concrete Adaptee
    static class VlcPlayer implements AdvancedMediaPlayer {
        @Override
        public void playVlc(String fileName) {
            System.out.println("   Playing VLC file: " + fileName);
        }

        @Override
        public void playMp4(String fileName) {
            // Do nothing
        }
    }

    static class Mp4Player implements AdvancedMediaPlayer {
        @Override
        public void playVlc(String fileName) {
            // Do nothing
        }

        @Override
        public void playMp4(String fileName) {
            System.out.println("   Playing MP4 file: " + fileName);
        }
    }

    // Adapter class
    static class MediaAdapter implements MediaPlayer {
        private AdvancedMediaPlayer advancedPlayer;

        public MediaAdapter(String audioType) {
            if (audioType.equalsIgnoreCase("vlc")) {
                advancedPlayer = new VlcPlayer();
            } else if (audioType.equalsIgnoreCase("mp4")) {
                advancedPlayer = new Mp4Player();
            }
        }

        @Override
        public void play(String audioType, String fileName) {
            if (audioType.equalsIgnoreCase("vlc")) {
                advancedPlayer.playVlc(fileName);
            } else if (audioType.equalsIgnoreCase("mp4")) {
                advancedPlayer.playMp4(fileName);
            }
        }
    }

    // Concrete Target implementation
    static class AudioPlayer implements MediaPlayer {
        @Override
        public void play(String audioType, String fileName) {
            if (audioType.equalsIgnoreCase("mp3")) {
                System.out.println("   Playing MP3 file: " + fileName);
            } else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
                MediaAdapter adapter = new MediaAdapter(audioType);
                adapter.play(audioType, fileName);
            } else {
                System.out.println("   Invalid media type: " + audioType);
            }
        }
    }
}

