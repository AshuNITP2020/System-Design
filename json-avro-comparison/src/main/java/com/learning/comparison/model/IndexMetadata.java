package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents metadata for a database index
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexMetadata {
    
    @JsonProperty("index_name")
    private String indexName;
    
    @JsonProperty("columns")
    private List<String> columns;
    
    @JsonProperty("is_unique")
    private boolean isUnique;
    
    @JsonProperty("index_type")
    private String indexType;
}

