package com.summarizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.summarizer.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
