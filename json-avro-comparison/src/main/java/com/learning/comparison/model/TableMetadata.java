package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents complete metadata for a database table
 * This is what we'll serialize to JSON and Avro for comparison
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableMetadata {
    
    @JsonProperty("table_name")
    private String tableName;
    
    @JsonProperty("database_name")
    private String databaseName;
    
    @JsonProperty("schema_name")
    private String schemaName;
    
    @JsonProperty("table_type")
    private String tableType;
    
    @JsonProperty("columns")
    private List<ColumnMetadata> columns;
    
    @JsonProperty("indexes")
    private List<IndexMetadata> indexes;
    
    @JsonProperty("foreign_keys")
    private List<ForeignKeyMetadata> foreignKeys;
    
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

