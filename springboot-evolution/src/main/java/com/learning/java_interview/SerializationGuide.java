package com.learning.java_interview;

import java.io.*;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                                    SERIALIZATION - COMPLETE GUIDE                                              ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        1. WHAT IS SERIALIZATION?                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * DEFINITION:
 * Serialization is the process of converting an object's state (its data/fields) into a stream of bytes
 * that can be stored in a file, sent over a network, or stored in a database.
 *
 * DESERIALIZATION:
 * The reverse process - converting the stream of bytes back into an object with the same state.
 *
 * ANALOGY:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Think of serialization like:                                                                                   │
 * │ • PACKING: Converting a 3D object (your Java object) into a flat box (byte stream) for shipping              │
 * │ • UNPACKING: Opening the box and reconstructing the 3D object (deserialization)                               │
 * │                                                                                                                │
 * │ Your object in memory:                                                                                        │
 * │   Person { name: "John", age: 30, address: "123 Main St" }                                                    │
 * │                                                                                                                │
 * │ After serialization (byte stream):                                                                            │
 * │   [binary data: ac ed 00 05 73 72 00 0a 50 65 72 73 6f 6e ...]                                                │
 * │                                                                                                                │
 * │ After deserialization:                                                                                        │
 * │   Person { name: "John", age: 30, address: "123 Main St" }  ← Same object!                                   │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        2. WHY IS SERIALIZATION NEEDED?                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * USE CASE 1: PERSISTENCE (Saving to File/Database)
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Scenario: You want to save your game progress, user settings, or application state                            │
 * │                                                                                                                │
 * │ Without Serialization:                                                                                        │
 * │   • You'd have to manually write each field to a file                                                          │
 * │   • Complex objects with nested references become very difficult                                               │
 * │   • Error-prone and time-consuming                                                                            │
 * │                                                                                                                │
 * │ With Serialization:                                                                                           │
 * │   • One line: objectOutputStream.writeObject(gameState);                                                      │
 * │   • Entire object graph saved automatically                                                                    │
 * │   • Easy to restore later                                                                                     │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * USE CASE 2: NETWORK COMMUNICATION (Sending Objects Over Network)
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Scenario: Client-server applications, microservices, RMI (Remote Method Invocation)                         │
 * │                                                                                                                │
 * │ Example:                                                                                                       │
 * │   Client sends: User object with login credentials                                                            │
 * │   Server receives: Same User object, validates, sends back response                                           │
 * │                                                                                                                │
 * │ Without Serialization:                                                                                        │
 * │   • Can't send objects directly over network                                                                  │
 * │   • Would need to convert to JSON/XML manually                                                                 │
 * │                                                                                                                │
 * │ With Serialization:                                                                                           │
 * │   • Objects can be sent as byte streams                                                                        │
 * │   • Automatically handled by Java RMI, socket programming                                                      │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * USE CASE 3: CACHING (Storing Objects in Cache)
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Scenario: Store expensive computation results, session data in Redis, Memcached                              │
 * │                                                                                                                │
 * │ Example:                                                                                                       │
 * │   • Cache database query results                                                                               │
 * │   • Store session objects                                                                                     │
 * │   • Save computed values for later use                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * USE CASE 4: DEEP COPYING (Cloning Objects)
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Scenario: Create an exact copy of an object (including all nested objects)                                   │
 * │                                                                                                                │
 * │ Example:                                                                                                       │
 * │   Person original = new Person("John", new Address("123 Main St"));                                           │
 * │   Person copy = serialize and deserialize → completely independent copy                                       │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * USE CASE 5: DISTRIBUTED SYSTEMS
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Scenario: Microservices, distributed caching, message queues                                                  │
 * │                                                                                                                │
 * │ Example:                                                                                                       │
 * │   • Send messages between services                                                                            │
 * │   • Replicate data across nodes                                                                               │
 * │   • Store state in distributed systems                                                                        │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        3. HOW SERIALIZATION WORKS                                              ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * STEP-BY-STEP PROCESS:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  SERIALIZATION (Object → Bytes):                                                                              │
 * │  ────────────────────────────────────────────────────────────────────────────────────────────────────────────── │
 * │  1. Check if class implements Serializable interface                                                          │
 * │  2. If yes, write class metadata (class name, serialVersionUID)                                               │
 * │  3. Write all non-transient, non-static fields                                                                │
 * │  4. Recursively serialize all referenced objects (object graph)                                              │
 * │  5. Write special markers (end of object, null references)                                                   │
 * │                                                                                                                │
 * │  DESERIALIZATION (Bytes → Object):                                                                            │
 * │  ────────────────────────────────────────────────────────────────────────────────────────────────────────────── │
 * │  1. Read class metadata                                                                                      │
 * │  2. Verify serialVersionUID matches (if specified)                                                           │
 * │  3. Create new instance (bypasses constructor!)                                                              │
 * │  4. Restore all non-transient, non-static fields                                                             │
 * │  5. Recursively deserialize all referenced objects                                                            │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * IMPORTANT NOTES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ⚠️  Constructor is NOT called during deserialization!                                                          │
 * │    • Object is created using reflection                                                                        │
 * │    • Fields are set directly                                                                                   │
 * │    • This is why readObject() and writeObject() methods exist                                                  │
 * │                                                                                                                │
 * │ ⚠️  Static fields are NOT serialized                                                                          │
 * │    • Static = class-level, not instance-level                                                                 │
 * │                                                                                                                │
 * │ ⚠️  Transient fields are NOT serialized                                                                        │
 * │    • Use transient for sensitive data (passwords) or temporary data                                            │
 * │                                                                                                                │
 * │ ⚠️  Object graph is serialized                                                                                 │
 * │    • If Person has Address, Address is also serialized                                                        │
 * │    • Circular references are handled (with special markers)                                                    │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        4. THE SERIALIZABLE INTERFACE                                            ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHAT IS IT?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Serializable is a MARKER INTERFACE (no methods to implement)                                                  │
 * │                                                                                                                │
 * │ public interface Serializable {                                                                               │
 * │     // Empty! No methods                                                                                      │
 * │ }                                                                                                              │
 * │                                                                                                                │
 * │ Purpose: Tells JVM "This class can be serialized"                                                             │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * HOW TO USE:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Simply implement Serializable:                                                                                │
 * │                                                                                                                │
 * │ class Person implements Serializable {                                                                        │
 * │     private String name;                                                                                      │
 * │     private int age;                                                                                          │
 * │     // ...                                                                                                    │
 * │ }                                                                                                              │
 * │                                                                                                                │
 * │ That's it! Now Person can be serialized.                                                                       │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * WHAT HAPPENS IF YOU DON'T IMPLEMENT IT?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ java.io.NotSerializableException will be thrown!                                                              │
 * │                                                                                                                │
 * │ Example:                                                                                                       │
 * │   Person person = new Person("John", 30);                                                                     │
 * │   ObjectOutputStream oos = new ObjectOutputStream(...);                                                      │
 * │   oos.writeObject(person);  // ❌ NotSerializableException if Person doesn't implement Serializable          │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        5. SERIALVERSIONUID - VERSION CONTROL                                   ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHAT IS IT?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ serialVersionUID is a unique identifier for a serialized class version                                        │
 * │                                                                                                                │
 * │ private static final long serialVersionUID = 1L;                                                               │
 * │                                                                                                                │
 * │ Purpose: Ensures compatibility between serialized and deserialized versions                                   │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * WHY IS IT IMPORTANT?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Scenario:                                                                                                      │
 * │                                                                                                                │
 * │ Version 1 (Today):                                                                                             │
 * │   class Person implements Serializable {                                                                       │
 * │       private String name;                                                                                     │
 * │       private int age;                                                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │ You serialize: Person p = new Person("John", 30);                                                             │
 * │ Save to file: person.ser                                                                                      │
 * │                                                                                                                │
 * │ Version 2 (Tomorrow - you add a field):                                                                       │
 * │   class Person implements Serializable {                                                                       │
 * │       private String name;                                                                                    │
 * │       private int age;                                                                                        │
 * │       private String email;  // NEW FIELD                                                                     │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │ Problem:                                                                                                       │
 * │   • JVM generates different serialVersionUID for Version 2                                                    │
 * │   • When deserializing old file, JVM sees different UID                                                       │
 * │   • Throws InvalidClassException: "local class incompatible"                                                 │
 * │                                                                                                                │
 * │ Solution:                                                                                                      │
 * │   • Explicitly declare serialVersionUID in Version 1                                                           │
 * │   • Keep same UID in Version 2                                                                                │
 * │   • Now deserialization works! (email will be null/default)                                                    │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * BEST PRACTICE:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ALWAYS declare serialVersionUID explicitly!                                                                   │
 * │                                                                                                                │
 * │ Why?                                                                                                           │
 * │   • Prevents InvalidClassException when class changes                                                          │
 * │   • Gives you control over version compatibility                                                              │
 * │   • JVM-generated UID changes with ANY class modification (even adding a comment!)                           │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        6. TRANSIENT KEYWORD                                                     ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHAT IS TRANSIENT?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ transient keyword marks a field to be EXCLUDED from serialization                                            │
 * │                                                                                                                │
 * │ Example:                                                                                                       │
 * │   class User implements Serializable {                                                                         │
 * │       private String username;                                                                                 │
 * │       private transient String password;  // ← NOT serialized!                                                │
 * │       private int age;                                                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │ When serialized:                                                                                              │
 * │   • username: serialized ✓                                                                                    │
 * │   • password: NOT serialized ✗ (security!)                                                                     │
 * │   • age: serialized ✓                                                                                         │
 * │                                                                                                                │
 * │ When deserialized:                                                                                            │
 * │   • password will be null (or default value)                                                                   │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * WHEN TO USE TRANSIENT?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ 1. SENSITIVE DATA: Passwords, credit card numbers, tokens                                                     │
 * │ 2. TEMPORARY DATA: Cache, computed values, timestamps                                                         │
 * │ 3. NON-SERIALIZABLE FIELDS: Fields that don't implement Serializable                                         │
 * │ 4. LARGE DATA: Fields that can be recomputed easily                                                           │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        7. CUSTOM SERIALIZATION                                                  ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * CUSTOM WRITE/READ METHODS:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ You can customize serialization by implementing:                                                               │
 * │                                                                                                                │
 * │ private void writeObject(ObjectOutputStream oos) throws IOException {                                         │
 * │     // Custom serialization logic                                                                              │
 * │     oos.defaultWriteObject();  // Write normal fields                                                         │
 * │     // Add custom data                                                                                        │
 * │ }                                                                                                              │
 * │                                                                                                                │
 * │ private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {                  │
 * │     // Custom deserialization logic                                                                            │
 * │     ois.defaultReadObject();  // Read normal fields                                                           │
 * │     // Restore custom data                                                                                    │
 * │ }                                                                                                              │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * USE CASES FOR CUSTOM SERIALIZATION:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ • Encrypt sensitive data before serialization                                                                 │
 * │ • Compress data                                                                                               │
 * │ • Add validation during deserialization                                                                        │
 * │ • Handle version compatibility manually                                                                       │
 * │ • Serialize non-serializable fields                                                                           │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        8. COMMON PITFALLS & BEST PRACTICES                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * PITFALL 1: Forgetting serialVersionUID
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ❌ BAD:                                                                                                        │
 * │   class Person implements Serializable { }  // JVM generates UID, breaks on any change                      │
 * │                                                                                                                │
 * │ ✓ GOOD:                                                                                                        │
 * │   class Person implements Serializable {                                                                       │
 * │       private static final long serialVersionUID = 1L;                                                        │
 * │   }                                                                                                            │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * PITFALL 2: Serializing Sensitive Data
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ❌ BAD:                                                                                                        │
 * │   class User implements Serializable {                                                                         │
 * │       private String password;  // Will be serialized!                                                        │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │ ✓ GOOD:                                                                                                        │
 * │   class User implements Serializable {                                                                        │
 * │       private transient String password;  // Excluded from serialization                                      │
 * │   }                                                                                                            │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * PITFALL 3: Inner Classes
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ❌ PROBLEM:                                                                                                    │
 * │   class Outer {                                                                                               │
 * │       class Inner implements Serializable { }  // Inner class has implicit reference to Outer!              │
 * │   }                                                                                                            │
 * │                                                                                                                │
 * │ ✓ SOLUTION:                                                                                                    │
 * │   class Outer {                                                                                               │
 * │       static class Inner implements Serializable { }  // Static inner class                                   │
 * │   }                                                                                                            │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * PITFALL 4: Inheritance Issues
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ • If parent class is Serializable, child is automatically Serializable                                        │
 * │ • If parent is NOT Serializable, child must handle parent fields manually                                     │
 * │ • All non-transient fields in inheritance hierarchy are serialized                                            │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * BEST PRACTICES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ✓ Always declare serialVersionUID explicitly                                                                  │
 * │ ✓ Mark sensitive fields as transient                                                                          │
 * │ ✓ Use static inner classes if you need to serialize inner classes                                             │
 * │ ✓ Consider using Externalizable for better control (advanced)                                                 │
 * │ ✓ Validate data during deserialization                                                                         │
 * │ ✓ Be careful with object graphs (circular references)                                                         │
 * │ ✓ Consider alternatives: JSON (Jackson/Gson), XML, Protocol Buffers for cross-language compatibility         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        9. ALTERNATIVES TO JAVA SERIALIZATION                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHY CONSIDER ALTERNATIVES?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Java Serialization has limitations:                                                                           │
 * │ • Java-specific (not cross-language)                                                                          │
 * │ • Security vulnerabilities (can execute arbitrary code)                                                       │
 * │ • Performance issues (slow, large size)                                                                        │
 * │ • Version compatibility issues                                                                                 │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * POPULAR ALTERNATIVES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ 1. JSON (Jackson, Gson)                                                                                       │
 * │    • Human-readable, cross-language, widely supported                                                         │
 * │                                                                                                                │
 * │ 2. Protocol Buffers (protobuf)                                                                                │
 * │    • Google's binary format, efficient, cross-language                                                        │
 * │                                                                                                                │
 * │ 3. Avro                                                                                                        │
 * │    • Apache's data serialization, schema evolution                                                            │
 * │                                                                                                                │
 * │ 4. MessagePack                                                                                                 │
 * │    • Binary JSON, compact, fast                                                                               │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * @author Java Interview Guide
 */
public class SerializationGuide {

    // ===================================================================
    // EXAMPLE 1: Basic Serialization
    // ===================================================================
    
    static class Person implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String name;
        private int age;
        private transient String password;  // Won't be serialized
        
        public Person(String name, int age, String password) {
            this.name = name;
            this.age = age;
            this.password = password;
        }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + ", password='" + password + "'}";
        }
    }
    
    public static void basicSerializationExample() throws IOException, ClassNotFoundException {
        System.out.println("\n=== BASIC SERIALIZATION EXAMPLE ===");
        
        // Create object
        Person person = new Person("John Doe", 30, "secret123");
        System.out.println("Original: " + person);
        
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(person);
        oos.close();
        
        System.out.println("Serialized to " + baos.size() + " bytes");
        
        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Person deserialized = (Person) ois.readObject();
        ois.close();
        
        System.out.println("Deserialized: " + deserialized);
        System.out.println("Note: password is null (transient field)");
    }
    
    // ===================================================================
    // EXAMPLE 2: File Serialization
    // ===================================================================
    
    public static void fileSerializationExample() throws IOException, ClassNotFoundException {
        System.out.println("\n=== FILE SERIALIZATION EXAMPLE ===");
        
        Person person = new Person("Jane Smith", 25, "password456");
        String filename = "person.ser";
        
        // Serialize to file
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(person);
        oos.close();
        fos.close();
        
        System.out.println("Saved to file: " + filename);
        
        // Deserialize from file
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Person loaded = (Person) ois.readObject();
        ois.close();
        fis.close();
        
        System.out.println("Loaded from file: " + loaded);
        
        // Clean up
        new File(filename).delete();
    }
    
    // ===================================================================
    // EXAMPLE 3: Object Graph Serialization
    // ===================================================================
    
    static class Address implements Serializable {
        private static final long serialVersionUID = 1L;
        private String street;
        private String city;
        
        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }
        
        @Override
        public String toString() {
            return street + ", " + city;
        }
    }
    
    static class PersonWithAddress implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Address address;  // Nested object
        
        public PersonWithAddress(String name, Address address) {
            this.name = name;
            this.address = address;
        }
        
        @Override
        public String toString() {
            return "PersonWithAddress{name='" + name + "', address=" + address + "}";
        }
    }
    
    public static void objectGraphExample() throws IOException, ClassNotFoundException {
        System.out.println("\n=== OBJECT GRAPH SERIALIZATION EXAMPLE ===");
        
        Address address = new Address("123 Main St", "New York");
        PersonWithAddress person = new PersonWithAddress("Bob", address);
        
        System.out.println("Original: " + person);
        
        // Serialize (address is automatically serialized too!)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(person);
        oos.close();
        
        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        PersonWithAddress deserialized = (PersonWithAddress) ois.readObject();
        ois.close();
        
        System.out.println("Deserialized: " + deserialized);
        System.out.println("Note: Address object was automatically serialized!");
    }
    
    // ===================================================================
    // EXAMPLE 4: Custom Serialization
    // ===================================================================
    
    static class SecureUser implements Serializable {
        private static final long serialVersionUID = 1L;
        private String username;
        private transient String password;  // Not serialized by default
        
        public SecureUser(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        // Custom serialization - encrypt password
        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();  // Serialize normal fields
            // Encrypt and write password (simple XOR for demo)
            String encrypted = encrypt(password);
            oos.writeObject(encrypted);
        }
        
        // Custom deserialization - decrypt password
        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();  // Deserialize normal fields
            // Read and decrypt password
            String encrypted = (String) ois.readObject();
            this.password = decrypt(encrypted);
        }
        
        private String encrypt(String data) {
            // Simple XOR encryption (NOT secure, just for demo!)
            char[] chars = data.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                chars[i] = (char) (chars[i] ^ 0x42);
            }
            return new String(chars);
        }
        
        private String decrypt(String data) {
            return encrypt(data);  // XOR is symmetric
        }
        
        public String getPassword() {
            return password;
        }
        
        @Override
        public String toString() {
            return "SecureUser{username='" + username + "', password='" + password + "'}";
        }
    }
    
    public static void customSerializationExample() throws IOException, ClassNotFoundException {
        System.out.println("\n=== CUSTOM SERIALIZATION EXAMPLE ===");
        
        SecureUser user = new SecureUser("admin", "secret123");
        System.out.println("Original: " + user);
        
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(user);
        oos.close();
        
        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SecureUser loaded = (SecureUser) ois.readObject();
        ois.close();
        
        System.out.println("Deserialized: " + loaded);
        System.out.println("Note: Password was encrypted during serialization!");
    }
    
    // ===================================================================
    // EXAMPLE 5: serialVersionUID Importance
    // ===================================================================
    
    static class Version1 implements Serializable {
        // No serialVersionUID - JVM will generate one
        @SuppressWarnings("unused")
        private String name;
        
        public Version1(String name) {
            this.name = name;
        }
    }
    
    static class Version2 implements Serializable {
        private static final long serialVersionUID = 1L;  // Explicit UID
        @SuppressWarnings("unused")
        private String name;
        
        public Version2(String name) {
            this.name = name;
        }
    }
    
    public static void serialVersionUIDExample() throws IOException, ClassNotFoundException {
        System.out.println("\n=== serialVersionUID EXAMPLE ===");
        
        // Version with explicit UID
        Version2 v2 = new Version2("Test");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(v2);
        oos.close();
        
        System.out.println("Serialized Version2 with explicit serialVersionUID");
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unused")
        Version2 loaded = (Version2) ois.readObject();
        ois.close();
        
        System.out.println("Deserialized successfully!");
        System.out.println("If you change the class and keep same UID, it will still work.");
    }
    
    // ===================================================================
    // MAIN METHOD - RUN ALL EXAMPLES
    // ===================================================================
    
    public static void main(String[] args) {
        try {
            basicSerializationExample();
            fileSerializationExample();
            objectGraphExample();
            customSerializationExample();
            serialVersionUIDExample();
            
            System.out.println("\n=== SUMMARY ===");
            System.out.println("✓ Serialization converts objects to byte streams");
            System.out.println("✓ Deserialization converts byte streams back to objects");
            System.out.println("✓ Use Serializable interface (marker interface)");
            System.out.println("✓ Always declare serialVersionUID explicitly");
            System.out.println("✓ Use transient for sensitive/temporary fields");
            System.out.println("✓ Custom serialization with writeObject/readObject");
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

