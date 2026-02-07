package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Flattened column representation with table reference
 * Used when storing columns separately
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatColumn {
    
    @JsonProperty("table_name")
    private String tableName;
    
    @JsonProperty("database_name")
    private String databaseName;
    
    @JsonProperty("schema_name")
    private String schemaName;
    
    @JsonProperty("column_name")
    private String columnName;
    
    @JsonProperty("data_type")
    private String dataType;
    
    @JsonProperty("is_nullable")
    private boolean isNullable;
    
    @JsonProperty("is_primary_key")
    private boolean isPrimaryKey;
    
    @JsonProperty("default_value")
    private String defaultValue;
    
    @JsonProperty("column_size")
    private Integer columnSize;
    
    @JsonProperty("precision")
    private Integer precision;
    
    @JsonProperty("scale")
    private Integer scale;
    
    @JsonProperty("column_comment")
    private String columnComment;
}

