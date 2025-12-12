package com.challenge.controller;

import com.challenge.dto.DocumentRequest;
import com.challenge.dto.DocumentResponse;
import com.challenge.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ⚠️ SECURITY CHALLENGE: This controller has NO security!
 * 
 * YOUR TASKS:
 * 1. Add authentication - only logged-in users can access these endpoints
 * 2. Add authorization - implement proper access control
 * 3. Get the current user from the security context
 * 
 * REQUIREMENTS:
 * - POST /documents → Only authenticated users with 'document:write' permission
 * - GET /documents → Only authenticated users (returns their accessible documents)
 * - GET /documents/{id} → Check if user has access to this specific document
 * - PUT /documents/{id} → Only owner can update
 * - DELETE /documents/{id} → Only owner OR admin can delete
 * - GET /documents/admin/all → Only ADMIN role
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Create a new document
     * TODO: Add security - require authentication and 'document:write' permission
     * TODO: Get the authenticated user and pass to service
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDocument(
            @Valid @RequestBody DocumentRequest request) {
        
        // ⚠️ PROBLEM: We don't know who the current user is!
        // TODO: Get authenticated user from SecurityContext
        Long currentUserId = 1L; // HARDCODED - THIS IS WRONG!
        
        DocumentResponse response = documentService.createDocument(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Document created", "data", response));
    }

    /**
     * Get all documents accessible to the current user
     * TODO: Add authentication requirement
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyDocuments() {
        
        // ⚠️ PROBLEM: Anyone can call this without logging in!
        Long currentUserId = 1L; // HARDCODED - THIS IS WRONG!
        
        List<DocumentResponse> documents = documentService.getAccessibleDocuments(currentUserId);
        return ResponseEntity.ok(Map.of("success", true, "data", documents));
    }

    /**
     * Get a specific document by ID
     * TODO: Check if user has permission to view this document
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentById(@PathVariable Long id) {
        
        // ⚠️ PROBLEM: No access control! Anyone can view any document!
        Long currentUserId = 1L; // HARDCODED - THIS IS WRONG!
        
        DocumentResponse document = documentService.getDocumentById(id, currentUserId);
        return ResponseEntity.ok(Map.of("success", true, "data", document));
    }

    /**
     * Update a document
     * TODO: Only the owner should be able to update their document
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest request) {
        
        // ⚠️ PROBLEM: Anyone can update any document!
        Long currentUserId = 1L; // HARDCODED - THIS IS WRONG!
        
        DocumentResponse response = documentService.updateDocument(id, request, currentUserId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Document updated", "data", response));
    }

    /**
     * Delete a document
     * TODO: Only owner OR admin can delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable Long id) {
        
        // ⚠️ PROBLEM: Anyone can delete any document!
        Long currentUserId = 1L; // HARDCODED - THIS IS WRONG!
        
        documentService.deleteDocument(id, currentUserId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Document deleted"));
    }

    /**
     * Admin endpoint - get all documents in the system
     * TODO: Only users with ADMIN role should access this
     */
    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllDocuments() {
        
        // ⚠️ PROBLEM: Anyone can access admin functionality!
        
        List<DocumentResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(Map.of("success", true, "data", documents));
    }
}

