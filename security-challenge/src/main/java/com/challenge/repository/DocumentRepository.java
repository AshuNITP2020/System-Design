package com.challenge.repository;

import com.challenge.entity.Document;
import com.challenge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByOwner(User owner);
    
    @Query("SELECT d FROM Document d WHERE d.visibility = 'PUBLIC'")
    List<Document> findAllPublicDocuments();
    
    @Query("SELECT d FROM Document d WHERE d.owner = :user OR d.visibility = 'PUBLIC'")
    List<Document> findAccessibleDocuments(@Param("user") User user);
}

