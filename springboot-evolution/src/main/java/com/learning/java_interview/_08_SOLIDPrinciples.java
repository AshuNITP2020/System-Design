// package com.learning.java_interview;

// import java.util.*;

// /**
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                                                                                                ║
//  * ║                                  SOLID PRINCIPLES - INTERVIEW DEEP DIVE                                        ║
//  * ║                                                                                                                ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    SOLID OVERVIEW                                                              ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  *  │                                                                                                                │
//  *  │  S - Single Responsibility Principle (SRP)                                                                     │
//  *  │      "A class should have only one reason to change"                                                           │
//  *  │                                                                                                                │
//  *  │  O - Open/Closed Principle (OCP)                                                                               │
//  *  │      "Open for extension, closed for modification"                                                             │
//  *  │                                                                                                                │
//  *  │  L - Liskov Substitution Principle (LSP)                                                                       │
//  *  │      "Subtypes must be substitutable for their base types"                                                     │
//  *  │                                                                                                                │
//  *  │  I - Interface Segregation Principle (ISP)                                                                     │
//  *  │      "Clients should not depend on interfaces they don't use"                                                  │
//  *  │                                                                                                                │
//  *  │  D - Dependency Inversion Principle (DIP)                                                                      │
//  *  │      "Depend on abstractions, not concretions"                                                                 │
//  *  │                                                                                                                │
//  *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                          S - SINGLE RESPONSIBILITY PRINCIPLE                                                   ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  *  │                                                                                                                │
//  *  │  BAD (Multiple responsibilities):              GOOD (Single responsibility):                                   │
//  *  │                                                                                                                │
//  *  │  class Employee {                              class Employee {                                                │
//  *  │      String name;                                  String name;                                                │
//  *  │      double salary;                                double salary;                                              │
//  *  │                                                    // Only employee data                                       │
//  *  │      void calculatePay() { }    // Pay logic   }                                                               │
//  *  │      void saveToDatabase() { }  // Persistence                                                                 │
//  *  │      void generateReport() { }  // Reporting   class PayrollCalculator {                                       │
//  *  │  }                                                 double calculatePay(Employee e) { }                         │
//  *  │                                                }                                                               │
//  *  │  Reasons to change:                                                                                            │
//  *  │  1. Employee data structure                    class EmployeeRepository {                                      │
//  *  │  2. Pay calculation logic                          void save(Employee e) { }                                   │
//  *  │  3. Database schema                            }                                                               │
//  *  │  4. Report format                                                                                              │
//  *  │                                                class EmployeeReportGenerator {                                 │
//  *  │                                                    void generateReport(Employee e) { }                         │
//  *  │                                                }                                                               │
//  *  │                                                                                                                │
//  *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                              O - OPEN/CLOSED PRINCIPLE                                                         ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  *  │                                                                                                                │
//  *  │  BAD (Modifying existing code):                GOOD (Extending via new classes):                               │
//  *  │                                                                                                                │
//  *  │  class DiscountCalculator {                    interface DiscountStrategy {                                    │
//  *  │      double calculate(String type, double p) {     double apply(double price);                                 │
//  *  │          if (type.equals("REGULAR"))           }                                                               │
//  *  │              return p * 0.1;                                                                                   │
//  *  │          else if (type.equals("PREMIUM"))      class RegularDiscount implements DiscountStrategy {             │
//  *  │              return p * 0.2;                       double apply(double p) { return p * 0.1; }                  │
//  *  │          // Adding new type requires           }                                                               │
//  *  │          // modifying this class!                                                                              │
//  *  │      }                                         class PremiumDiscount implements DiscountStrategy {             │
//  *  │  }                                                 double apply(double p) { return p * 0.2; }                  │
//  *  │                                                }                                                               │
//  *  │                                                                                                                │
//  *  │                                                // Adding new discount type:                                    │
//  *  │                                                // Just create new class!                                       │
//  *  │                                                class VIPDiscount implements DiscountStrategy {                 │
//  *  │                                                    double apply(double p) { return p * 0.3; }                  │
//  *  │                                                }                                                               │
//  *  │                                                                                                                │
//  *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                            L - LISKOV SUBSTITUTION PRINCIPLE                                                   ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  *  │                                                                                                                │
//  *  │  BAD (Square violates Rectangle behavior):     GOOD (Proper abstraction):                                      │
//  *  │                                                                                                                │
//  *  │  class Rectangle {                             interface Shape {                                               │
//  *  │      int width, height;                            double area();                                              │
//  *  │      void setWidth(int w) { width = w; }       }                                                               │
//  *  │      void setHeight(int h) { height = h; }                                                                     │
//  *  │      int area() { return width * height; }     class Rectangle implements Shape {                              │
//  *  │  }                                                 int width, height;                                          │
//  *  │                                                    Rectangle(int w, int h) { ... }                             │
//  *  │  class Square extends Rectangle {                  double area() { return width * height; }                    │
//  *  │      void setWidth(int w) {                    }                                                               │
//  *  │          width = w;                                                                                            │
//  *  │          height = w; // ⚠️ Breaks LSP!         class Square implements Shape {                                 │
//  *  │      }                                             int side;                                                   │
//  *  │  }                                                 Square(int s) { side = s; }                                 │
//  *  │                                                    double area() { return side * side; }                       │
//  *  │  // Problem:                                   }                                                               │
//  *  │  Rectangle r = new Square();                                                                                   │
//  *  │  r.setWidth(5);                                // Both can be used wherever Shape is expected                  │
//  *  │  r.setHeight(4);                               // without breaking the program                                 │
//  *  │  r.area(); // Returns 16, not 20!                                                                              │
//  *  │                                                                                                                │
//  *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                            I - INTERFACE SEGREGATION PRINCIPLE                                                 ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  *  │                                                                                                                │
//  *  │  BAD (Fat interface):                          GOOD (Segregated interfaces):                                   │
//  *  │                                                                                                                │
//  *  │  interface Worker {                            interface Workable {                                            │
//  *  │      void work();                                  void work();                                                │
//  *  │      void eat();                               }                                                               │
//  *  │      void sleep();                                                                                             │
//  *  │  }                                             interface Eatable {                                             │
//  *  │                                                    void eat();                                                 │
//  *  │  class Robot implements Worker {               }                                                               │
//  *  │      void work() { /* OK */ }                                                                                  │
//  *  │      void eat() { /* ??? */ }  // Not needed!  interface Sleepable {                                           │
//  *  │      void sleep() { /* ??? */ }// Not needed!      void sleep();                                               │
//  *  │  }                                             }                                                               │
//  *  │                                                                                                                │
//  *  │                                                class Human implements Workable, Eatable, Sleepable {           │
//  *  │                                                    void work() { }                                             │
//  *  │                                                    void eat() { }                                              │
//  *  │                                                    void sleep() { }                                            │
//  *  │                                                }                                                               │
//  *  │                                                                                                                │
//  *  │                                                class Robot implements Workable {                               │
//  *  │                                                    void work() { }                                             │
//  *  │                                                    // No forced empty implementations!                         │
//  *  │                                                }                                                               │
//  *  │                                                                                                                │
//  *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                            D - DEPENDENCY INVERSION PRINCIPLE                                                  ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  *  │                                                                                                                │
//  *  │  BAD (High-level depends on low-level):        GOOD (Both depend on abstraction):                              │
//  *  │                                                                                                                │
//  *  │  class EmailService {                          interface NotificationService {                                 │
//  *  │      void sendEmail(String msg) { }                void send(String msg);                                      │
//  *  │  }                                             }                                                               │
//  *  │                                                                                                                │
//  *  │  class UserController {                        class EmailService implements NotificationService {             │
//  *  │      EmailService emailService;                    void send(String msg) { /* email logic */ }                 │
//  *  │                                                }                                                               │
//  *  │      UserController() {                                                                                        │
//  *  │          emailService = new EmailService();    class SMSService implements NotificationService {               │
//  *  │          // ⚠️ Tightly coupled!                    void send(String msg) { /* sms logic */ }                   │
//  *  │      }                                         }                                                               │
//  *  │                                                                                                                │
//  *  │      void notify(String msg) {                 class UserController {                                          │
//  *  │          emailService.sendEmail(msg);              NotificationService notificationService;                    │
//  *  │      }                                                                                                         │
//  *  │  }                                                 // Dependency Injection                                     │
//  *  │                                                    UserController(NotificationService ns) {                    │
//  *  │  // Can't easily switch to SMS!                        this.notificationService = ns;                          │
//  *  │  // Can't easily test!                             }                                                           │
//  *  │                                                                                                                │
//  *  │                                                    void notify(String msg) {                                   │
//  *  │                                                        notificationService.send(msg);                          │
//  *  │                                                    }                                                           │
//  *  │                                                }                                                               │
//  *  │                                                                                                                │
//  *  │                                                // Easy to switch, easy to test!                                │
//  *  │                                                new UserController(new EmailService());                         │
//  *  │                                                new UserController(new SMSService());                           │
//  *  │                                                new UserController(mockNotificationService);                    │
//  *  │                                                                                                                │
//  *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * @author Java Interview Guide
//  */
// public class _08_SOLIDPrinciples {

//     public static void main(String[] args) {
//         System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
//         System.out.println("                        SOLID PRINCIPLES DEMONSTRATION");
//         System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

//         // S - Single Responsibility
//         demonstrateSRP();

//         // O - Open/Closed
//         demonstrateOCP();

//         // L - Liskov Substitution
//         demonstrateLSP();

//         // I - Interface Segregation
//         demonstrateISP();

//         // D - Dependency Inversion
//         demonstrateDIP();
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                    S - SINGLE RESPONSIBILITY PRINCIPLE
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateSRP() {
//         System.out.println("▶ S - SINGLE RESPONSIBILITY PRINCIPLE:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");
//         System.out.println("   Each class has ONE responsibility, ONE reason to change.\n");

//         // Each class has single responsibility
//         Employee employee = new Employee("John", 50000);
//         PayrollService payroll = new PayrollService();
//         EmployeeRepository repository = new EmployeeRepository();
//         ReportService reportService = new ReportService();

//         double pay = payroll.calculatePay(employee);
//         System.out.println("   PayrollService calculated pay: $" + pay);

//         repository.save(employee);
//         reportService.generateReport(employee);

//         System.out.println();
//     }

//     // Each class has single responsibility
//     static class Employee {
//         String name;
//         double salary;

//         Employee(String name, double salary) {
//             this.name = name;
//             this.salary = salary;
//         }
//     }

//     static class PayrollService {
//         double calculatePay(Employee e) {
//             return e.salary / 12;  // Monthly pay
//         }
//     }

//     static class EmployeeRepository {
//         void save(Employee e) {
//             System.out.println("   EmployeeRepository saved: " + e.name);
//         }
//     }

//     static class ReportService {
//         void generateReport(Employee e) {
//             System.out.println("   ReportService generated report for: " + e.name);
//         }
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                    O - OPEN/CLOSED PRINCIPLE
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateOCP() {
//         System.out.println("▶ O - OPEN/CLOSED PRINCIPLE:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");
//         System.out.println("   Open for extension, closed for modification.\n");

//         // Adding new shapes doesn't require modifying AreaCalculator
//         List<Shape> shapes = Arrays.asList(
//                 new Circle(5),
//                 new Rectangle(4, 6),
//                 new Triangle(3, 4)  // New shape added without changing existing code
//         );

//         AreaCalculator calculator = new AreaCalculator();
//         for (Shape shape : shapes) {
//             System.out.println("   " + shape.getClass().getSimpleName() +
//                     " area: " + calculator.calculate(shape));
//         }

//         System.out.println();
//     }

//     // Abstraction
//     interface Shape {
//         double area();
//     }

//     // Implementations - can add new shapes without modifying existing code
//     static class Circle implements Shape {
//         double radius;
//         Circle(double r) { this.radius = r; }
//         public double area() { return Math.PI * radius * radius; }
//     }

//     static class Rectangle implements Shape {
//         double width, height;
//         Rectangle(double w, double h) { this.width = w; this.height = h; }
//         public double area() { return width * height; }
//     }

//     static class Triangle implements Shape {
//         double base, height;
//         Triangle(double b, double h) { this.base = b; this.height = h; }
//         public double area() { return 0.5 * base * height; }
//     }

//     // Calculator works with abstraction - never needs modification
//     static class AreaCalculator {
//         double calculate(Shape shape) {
//             return shape.area();
//         }
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                    L - LISKOV SUBSTITUTION PRINCIPLE
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateLSP() {
//         System.out.println("▶ L - LISKOV SUBSTITUTION PRINCIPLE:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");
//         System.out.println("   Subtypes must be substitutable for their base types.\n");

//         // All birds can be used wherever Bird is expected
//         List<Bird> birds = Arrays.asList(
//                 new Sparrow(),
//                 new Ostrich()
//         );

//         for (Bird bird : birds) {
//             bird.eat();  // All birds can eat
//         }

//         // Only flying birds can fly
//         List<FlyingBird> flyingBirds = Arrays.asList(
//                 new Sparrow(),
//                 new Eagle()
//         );

//         System.out.println("\n   Flying birds:");
//         for (FlyingBird bird : flyingBirds) {
//             bird.fly();  // All these can fly
//         }

//         System.out.println();
//     }

//     // Base class for all birds
//     static abstract class Bird {
//         abstract void eat();
//     }

//     // Interface for flying capability
//     interface FlyingBird {
//         void fly();
//     }

//     // Sparrow can fly
//     static class Sparrow extends Bird implements FlyingBird {
//         void eat() { System.out.println("   Sparrow eating seeds"); }
//         public void fly() { System.out.println("   Sparrow flying"); }
//     }

//     // Eagle can fly
//     static class Eagle extends Bird implements FlyingBird {
//         void eat() { System.out.println("   Eagle eating prey"); }
//         public void fly() { System.out.println("   Eagle soaring"); }
//     }

//     // Ostrich cannot fly - doesn't implement FlyingBird
//     static class Ostrich extends Bird {
//         void eat() { System.out.println("   Ostrich eating plants"); }
//         // No fly() method - LSP satisfied!
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                    I - INTERFACE SEGREGATION PRINCIPLE
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateISP() {
//         System.out.println("▶ I - INTERFACE SEGREGATION PRINCIPLE:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");
//         System.out.println("   Clients should not depend on interfaces they don't use.\n");

//         // Human implements all interfaces it needs
//         HumanWorker human = new HumanWorker("Alice");
//         human.work();
//         human.eat();
//         human.sleep();

//         System.out.println();

//         // Robot only implements what it can do
//         RobotWorker robot = new RobotWorker("R2D2");
//         robot.work();
//         robot.charge();  // Instead of eat/sleep

//         System.out.println();
//     }

//     // Segregated interfaces
//     interface Workable {
//         void work();
//     }

//     interface Eatable {
//         void eat();
//     }

//     interface Sleepable {
//         void sleep();
//     }

//     interface Chargeable {
//         void charge();
//     }

//     // Human implements what it needs
//     static class HumanWorker implements Workable, Eatable, Sleepable {
//         String name;
//         HumanWorker(String name) { this.name = name; }

//         public void work() { System.out.println("   " + name + " is working"); }
//         public void eat() { System.out.println("   " + name + " is eating"); }
//         public void sleep() { System.out.println("   " + name + " is sleeping"); }
//     }

//     // Robot only implements what it can do
//     static class RobotWorker implements Workable, Chargeable {
//         String name;
//         RobotWorker(String name) { this.name = name; }

//         public void work() { System.out.println("   " + name + " is working"); }
//         public void charge() { System.out.println("   " + name + " is charging"); }
//         // No forced empty eat() or sleep() methods!
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                    D - DEPENDENCY INVERSION PRINCIPLE
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateDIP() {
//         System.out.println("▶ D - DEPENDENCY INVERSION PRINCIPLE:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");
//         System.out.println("   Depend on abstractions, not concretions.\n");

//         // High-level module depends on abstraction
//         NotificationService emailService = new EmailNotification();
//         NotificationService smsService = new SMSNotification();
//         NotificationService pushService = new PushNotification();

//         // Easy to switch implementations
//         OrderProcessor processor1 = new OrderProcessor(emailService);
//         processor1.processOrder("ORDER-001");

//         OrderProcessor processor2 = new OrderProcessor(smsService);
//         processor2.processOrder("ORDER-002");

//         OrderProcessor processor3 = new OrderProcessor(pushService);
//         processor3.processOrder("ORDER-003");

//         System.out.println();
//     }

//     // Abstraction
//     interface NotificationService {
//         void send(String message);
//     }

//     // Low-level modules
//     static class EmailNotification implements NotificationService {
//         public void send(String message) {
//             System.out.println("   📧 Email sent: " + message);
//         }
//     }

//     static class SMSNotification implements NotificationService {
//         public void send(String message) {
//             System.out.println("   📱 SMS sent: " + message);
//         }
//     }

//     static class PushNotification implements NotificationService {
//         public void send(String message) {
//             System.out.println("   🔔 Push notification sent: " + message);
//         }
//     }

//     // High-level module depends on abstraction (interface)
//     static class OrderProcessor {
//         private NotificationService notificationService;

//         // Dependency Injection via constructor
//         OrderProcessor(NotificationService notificationService) {
//             this.notificationService = notificationService;
//         }

//         void processOrder(String orderId) {
//             // Process order logic...
//             notificationService.send("Order " + orderId + " processed successfully");
//         }
//     }
// }

