package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Flattened foreign key representation with table reference
 * Used when storing foreign keys separately
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatForeignKey {
    
    @JsonProperty("table_name")
    private String tableName;
    
    @JsonProperty("database_name")
    private String databaseName;
    
    @JsonProperty("schema_name")
    private String schemaName;
    
    @JsonProperty("constraint_name")
    private String constraintName;
    
    @JsonProperty("column_names")
    private List<String> columnNames;
    
    @JsonProperty("referenced_table")
    private String referencedTable;
    
    @JsonProperty("referenced_columns")
    private List<String> referencedColumns;
    
    @JsonProperty("on_delete")
    private String onDelete;
    
    @JsonProperty("on_update")
    private String onUpdate;
}

