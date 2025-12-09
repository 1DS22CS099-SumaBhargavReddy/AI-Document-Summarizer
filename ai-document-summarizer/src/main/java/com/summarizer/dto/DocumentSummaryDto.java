package com.summarizer.dto;

import java.util.List;

public class DocumentSummaryDto {

    private Long id;
    private String fileName;
    private List<String> sectionSummaries;
    private String overallSummary;

    public DocumentSummaryDto(Long id, String fileName, List<String> sectionSummaries, String overallSummary) {
        this.id = id;
        this.fileName = fileName;
        this.sectionSummaries = sectionSummaries;
        this.overallSummary = overallSummary;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getSectionSummaries() {
        return sectionSummaries;
    }

    public String getOverallSummary() {
        return overallSummary;
    }
}
