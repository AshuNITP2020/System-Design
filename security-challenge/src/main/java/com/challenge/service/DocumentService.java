package com.challenge.service;

import com.challenge.dto.DocumentRequest;
import com.challenge.dto.DocumentResponse;
import com.challenge.entity.Document;
import com.challenge.entity.DocumentVisibility;
import com.challenge.entity.Role;
import com.challenge.entity.User;
import com.challenge.repository.DocumentRepository;
import com.challenge.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DocumentResponse createDocument(DocumentRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setOwner(owner);
        // TODO: Set visibility from request

        Document saved = documentRepository.save(document);
        return mapToResponse(saved);
    }

    public List<DocumentResponse> getAccessibleDocuments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return documentRepository.findAccessibleDocuments(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(Long documentId, Long requestingUserId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Authorization check: User can view if:
        // 1. They are the owner
        // 2. Document is PUBLIC
        // 3. They are an ADMIN (can view all documents)
        boolean isOwner = document.getOwner().getId().equals(requestingUserId);
        boolean isPublic = document.getVisibility() == DocumentVisibility.PUBLIC;
        boolean isAdmin = requestingUser.getRole() == Role.ADMIN;

        if (!isOwner && !isPublic && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to view this document");
        }

        return mapToResponse(document);
    }

    @Transactional
    public DocumentResponse updateDocument(Long documentId, DocumentRequest request, Long requestingUserId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Authorization check: Only owner can update
        if (!document.getOwner().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("Only the document owner can update this document");
        }

        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        // TODO: Update visibility if needed

        Document updated = documentRepository.save(document);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteDocument(Long documentId, Long requestingUserId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Authorization check: Only owner OR admin can delete
        boolean isOwner = document.getOwner().getId().equals(requestingUserId);
        boolean isAdmin = requestingUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Only the document owner or an admin can delete this document");
        }

        documentRepository.delete(document);
    }

    public List<DocumentResponse> getAllDocuments() {
        // This should only be callable by admins (enforce at controller level)
        return documentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DocumentResponse mapToResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .ownerUsername(document.getOwner().getUsername())
                .visibility(document.getVisibility().name())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

