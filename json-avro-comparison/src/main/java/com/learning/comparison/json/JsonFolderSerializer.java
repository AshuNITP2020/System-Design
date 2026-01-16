package com.learning.comparison.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.learning.comparison.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes table metadata to separate folders for tables, columns, indexes, and foreign keys
 * This demonstrates a data lake / warehouse organizational pattern
 */
public class JsonFolderSerializer {
    
    private final ObjectMapper objectMapper;
    
    public JsonFolderSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Serialize metadata to separate folders
     * 
     * Structure:
     * outputDir/
     *   ├── tables/metadata.json
     *   ├── columns/metadata.json
     *   ├── indexes/metadata.json
     *   └── foreign_keys/metadata.json
     */
    public void serializeToFolders(List<TableMetadata> tables, Path outputDir) throws IOException {
        // Create folder structure
        Path tablesDir = outputDir.resolve("tables");
        Path columnsDir = outputDir.resolve("columns");
        Path indexesDir = outputDir.resolve("indexes");
        Path foreignKeysDir = outputDir.resolve("foreign_keys");
        
        Files.createDirectories(tablesDir);
        Files.createDirectories(columnsDir);
        Files.createDirectories(indexesDir);
        Files.createDirectories(foreignKeysDir);
        
        // Flatten data
        List<FlatTable> flatTables = new ArrayList<>();
        List<FlatColumn> flatColumns = new ArrayList<>();
        List<FlatIndex> flatIndexes = new ArrayList<>();
        List<FlatForeignKey> flatForeignKeys = new ArrayList<>();
        
        for (TableMetadata table : tables) {
            // Flatten table
            flatTables.add(FlatTable.builder()
                    .tableName(table.getTableName())
                    .databaseName(table.getDatabaseName())
                    .schemaName(table.getSchemaName())
                    .tableType(table.getTableType())
                    .rowCount(table.getRowCount())
                    .tableSizeBytes(table.getTableSizeBytes())
                    .createdAt(table.getCreatedAt())
                    .lastModified(table.getLastModified())
                    .tableComment(table.getTableComment())
                    .build());
            
            // Flatten columns
            for (ColumnMetadata column : table.getColumns()) {
                flatColumns.add(FlatColumn.builder()
                        .tableName(table.getTableName())
                        .databaseName(table.getDatabaseName())
                        .schemaName(table.getSchemaName())
                        .columnName(column.getColumnName())
                        .dataType(column.getDataType())
                        .isNullable(column.isNullable())
                        .isPrimaryKey(column.isPrimaryKey())
                        .defaultValue(column.getDefaultValue())
                        .columnSize(column.getColumnSize())
                        .precision(column.getPrecision())
                        .scale(column.getScale())
                        .columnComment(column.getColumnComment())
                        .build());
            }
            
            // Flatten indexes
            for (IndexMetadata index : table.getIndexes()) {
                flatIndexes.add(FlatIndex.builder()
                        .tableName(table.getTableName())
                        .databaseName(table.getDatabaseName())
                        .schemaName(table.getSchemaName())
                        .indexName(index.getIndexName())
                        .columns(index.getColumns())
                        .isUnique(index.isUnique())
                        .indexType(index.getIndexType())
                        .build());
            }
            
            // Flatten foreign keys
            for (ForeignKeyMetadata fk : table.getForeignKeys()) {
                flatForeignKeys.add(FlatForeignKey.builder()
                        .tableName(table.getTableName())
                        .databaseName(table.getDatabaseName())
                        .schemaName(table.getSchemaName())
                        .constraintName(fk.getConstraintName())
                        .columnNames(fk.getColumnNames())
                        .referencedTable(fk.getReferencedTable())
                        .referencedColumns(fk.getReferencedColumns())
                        .onDelete(fk.getOnDelete())
                        .onUpdate(fk.getOnUpdate())
                        .build());
            }
        }
        
        // Write to files
        writeToFile(flatTables, tablesDir.resolve("metadata.json"));
        writeToFile(flatColumns, columnsDir.resolve("metadata.json"));
        writeToFile(flatIndexes, indexesDir.resolve("metadata.json"));
        writeToFile(flatForeignKeys, foreignKeysDir.resolve("metadata.json"));
    }
    
    private void writeToFile(Object data, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            objectMapper.writeValue(writer, data);
        }
    }
    
    /**
     * Deserialize metadata from separate folders
     */
    public List<TableMetadata> deserializeFromFolders(Path outputDir) throws IOException {
        Path tablesPath = outputDir.resolve("tables/metadata.json");
        Path columnsPath = outputDir.resolve("columns/metadata.json");
        Path indexesPath = outputDir.resolve("indexes/metadata.json");
        Path foreignKeysPath = outputDir.resolve("foreign_keys/metadata.json");
        
        // Read all data
        List<FlatTable> flatTables = objectMapper.readValue(
                Files.newBufferedReader(tablesPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FlatTable.class));
        
        List<FlatColumn> flatColumns = objectMapper.readValue(
                Files.newBufferedReader(columnsPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FlatColumn.class));
        
        List<FlatIndex> flatIndexes = objectMapper.readValue(
                Files.newBufferedReader(indexesPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FlatIndex.class));
        
        List<FlatForeignKey> flatForeignKeys = objectMapper.readValue(
                Files.newBufferedReader(foreignKeysPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FlatForeignKey.class));
        
        // Reconstruct table metadata
        List<TableMetadata> tables = new ArrayList<>();
        
        for (FlatTable flatTable : flatTables) {
            String tableKey = flatTable.getDatabaseName() + "." + 
                            flatTable.getSchemaName() + "." + 
                            flatTable.getTableName();
            
            // Find columns for this table
            List<ColumnMetadata> columns = new ArrayList<>();
            for (FlatColumn flatColumn : flatColumns) {
                String colTableKey = flatColumn.getDatabaseName() + "." + 
                                   flatColumn.getSchemaName() + "." + 
                                   flatColumn.getTableName();
                if (colTableKey.equals(tableKey)) {
                    columns.add(ColumnMetadata.builder()
                            .columnName(flatColumn.getColumnName())
                            .dataType(flatColumn.getDataType())
                            .isNullable(flatColumn.isNullable())
                            .isPrimaryKey(flatColumn.isPrimaryKey())
                            .defaultValue(flatColumn.getDefaultValue())
                            .columnSize(flatColumn.getColumnSize())
                            .precision(flatColumn.getPrecision())
                            .scale(flatColumn.getScale())
                            .columnComment(flatColumn.getColumnComment())
                            .build());
                }
            }
            
            // Find indexes for this table
            List<IndexMetadata> indexes = new ArrayList<>();
            for (FlatIndex flatIndex : flatIndexes) {
                String idxTableKey = flatIndex.getDatabaseName() + "." + 
                                   flatIndex.getSchemaName() + "." + 
                                   flatIndex.getTableName();
                if (idxTableKey.equals(tableKey)) {
                    indexes.add(IndexMetadata.builder()
                            .indexName(flatIndex.getIndexName())
                            .columns(flatIndex.getColumns())
                            .isUnique(flatIndex.isUnique())
                            .indexType(flatIndex.getIndexType())
                            .build());
                }
            }
            
            // Find foreign keys for this table
            List<ForeignKeyMetadata> foreignKeys = new ArrayList<>();
            for (FlatForeignKey flatFk : flatForeignKeys) {
                String fkTableKey = flatFk.getDatabaseName() + "." + 
                                  flatFk.getSchemaName() + "." + 
                                  flatFk.getTableName();
                if (fkTableKey.equals(tableKey)) {
                    foreignKeys.add(ForeignKeyMetadata.builder()
                            .constraintName(flatFk.getConstraintName())
                            .columnNames(flatFk.getColumnNames())
                            .referencedTable(flatFk.getReferencedTable())
                            .referencedColumns(flatFk.getReferencedColumns())
                            .onDelete(flatFk.getOnDelete())
                            .onUpdate(flatFk.getOnUpdate())
                            .build());
                }
            }
            
            // Reconstruct table
            tables.add(TableMetadata.builder()
                    .tableName(flatTable.getTableName())
                    .databaseName(flatTable.getDatabaseName())
                    .schemaName(flatTable.getSchemaName())
                    .tableType(flatTable.getTableType())
                    .columns(columns)
                    .indexes(indexes)
                    .foreignKeys(foreignKeys)
                    .rowCount(flatTable.getRowCount())
                    .tableSizeBytes(flatTable.getTableSizeBytes())
                    .createdAt(flatTable.getCreatedAt())
                    .lastModified(flatTable.getLastModified())
                    .tableComment(flatTable.getTableComment())
                    .build());
        }
        
        return tables;
    }
    
    /**
     * Calculate total size of all files in folders
     */
    public long getTotalSize(Path outputDir) throws IOException {
        long total = 0;
        total += Files.size(outputDir.resolve("tables/metadata.json"));
        total += Files.size(outputDir.resolve("columns/metadata.json"));
        total += Files.size(outputDir.resolve("indexes/metadata.json"));
        total += Files.size(outputDir.resolve("foreign_keys/metadata.json"));
        return total;
    }
}

