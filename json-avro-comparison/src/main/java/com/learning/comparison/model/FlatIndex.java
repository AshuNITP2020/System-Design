package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Flattened index representation with table reference
 * Used when storing indexes separately
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatIndex {
    
    @JsonProperty("table_name")
    private String tableName;
    
    @JsonProperty("database_name")
    private String databaseName;
    
    @JsonProperty("schema_name")
    private String schemaName;
    
    @JsonProperty("index_name")
    private String indexName;
    
    @JsonProperty("columns")
    private List<String> columns;
    
    @JsonProperty("is_unique")
    private boolean isUnique;
    
    @JsonProperty("index_type")
    private String indexType;
}

