package com.challenge.controller;

import com.challenge.dto.DocumentRequest;
import com.challenge.dto.DocumentResponse;
import com.challenge.entity.User;
import com.challenge.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Autowired
    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Create a new document
     * Requires authentication and 'document:write' permission
     */
    @PostMapping
    @PreAuthorize("hasAuthority('document:write')")
    public ResponseEntity<Map<String, Object>> createDocument(
            @Valid @RequestBody DocumentRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        DocumentResponse response = documentService.createDocument(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Document created", "data", response));
    }

    /**
     * Get all documents accessible to the current user
     * Requires authentication
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyDocuments(
            @AuthenticationPrincipal User currentUser) {
        
        List<DocumentResponse> documents = documentService.getAccessibleDocuments(currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true, "data", documents));
    }

    /**
     * Get a specific document by ID
     * Requires authentication - access control is handled in service layer
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        DocumentResponse document = documentService.getDocumentById(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true, "data", document));
    }

    /**
     * Update a document
     * Requires authentication - only owner can update (enforced in service layer)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        DocumentResponse response = documentService.updateDocument(id, request, currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true, "message", "Document updated", "data", response));
    }

    /**
     * Delete a document
     * Requires authentication - only owner OR admin can delete (enforced in service layer)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        documentService.deleteDocument(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true, "message", "Document deleted"));
    }

    /**
     * Admin endpoint - get all documents in the system
     * Requires ADMIN role
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllDocuments() {
        
        List<DocumentResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(Map.of("success", true, "data", documents));
    }
}

