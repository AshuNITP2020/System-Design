package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Flattened table representation (without nested columns/indexes/foreign keys)
 * Used when storing tables separately from their details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatTable {
    
    @JsonProperty("table_name")
    private String tableName;
    
    @JsonProperty("database_name")
    private String databaseName;
    
    @JsonProperty("schema_name")
    private String schemaName;
    
    @JsonProperty("table_type")
    private String tableType;
    
    @JsonProperty("row_count")
    private Long rowCount;
    
    @JsonProperty("table_size_bytes")
    private Long tableSizeBytes;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("last_modified")
    private String lastModified;
    
    @JsonProperty("table_comment")
    private String tableComment;
}

