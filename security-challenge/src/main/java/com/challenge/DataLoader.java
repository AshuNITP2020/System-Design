package com.challenge;

import com.challenge.entity.Document;
import com.challenge.entity.DocumentVisibility;
import com.challenge.entity.User;
import com.challenge.repository.DocumentRepository;
import com.challenge.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public DataLoader(UserRepository userRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public void run(String... args) {
        // Create sample users (passwords are plain text - INSECURE!)
        User john = new User();
        john.setUsername("john");
        john.setEmail("john@example.com");
        john.setPassword("password123"); // ⚠️ Plain text password!
        userRepository.save(john);

        User jane = new User();
        jane.setUsername("jane");
        jane.setEmail("jane@example.com");
        jane.setPassword("secret456"); // ⚠️ Plain text password!
        userRepository.save(jane);

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123"); // ⚠️ Plain text password!
        userRepository.save(admin);

        // Create sample documents
        Document doc1 = new Document();
        doc1.setTitle("John's Private Diary");
        doc1.setContent("Dear diary, my bank account PIN is 1234 and my SSN is 123-45-6789...");
        doc1.setOwner(john);
        doc1.setVisibility(DocumentVisibility.PRIVATE);
        documentRepository.save(doc1);

        Document doc2 = new Document();
        doc2.setTitle("Company Financial Report 2024");
        doc2.setContent("Q4 Revenue: $10M, Secret merger plans with XYZ Corp...");
        doc2.setOwner(admin);
        doc2.setVisibility(DocumentVisibility.PRIVATE);
        documentRepository.save(doc2);

        Document doc3 = new Document();
        doc3.setTitle("Public Announcement");
        doc3.setContent("Welcome to our platform!");
        doc3.setOwner(admin);
        doc3.setVisibility(DocumentVisibility.PUBLIC);
        documentRepository.save(doc3);

        Document doc4 = new Document();
        doc4.setTitle("Jane's Medical Records");
        doc4.setContent("Patient: Jane Doe, Diagnosis: Confidential medical information...");
        doc4.setOwner(jane);
        doc4.setVisibility(DocumentVisibility.PRIVATE);
        documentRepository.save(doc4);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚨 SAMPLE DATA LOADED - SECURITY VULNERABILITIES DEMO 🚨");
        System.out.println("=".repeat(60));
        System.out.println("Users created: john, jane, admin");
        System.out.println("Documents created: 4 (including PRIVATE ones!)");
        System.out.println("=".repeat(60) + "\n");
    }
}

