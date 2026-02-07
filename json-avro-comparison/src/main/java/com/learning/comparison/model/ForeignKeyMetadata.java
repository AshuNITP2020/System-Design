package com.learning.comparison.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents metadata for a foreign key constraint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeignKeyMetadata {
    
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

