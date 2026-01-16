package com.learning.comparison.avro;

import com.learning.comparison.model.*;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles Avro serialization and deserialization of table metadata
 */
public class AvroSerializer {
    
    private final Schema schema;
    
    public AvroSerializer() throws IOException {
        // Load schema from classpath
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("table_metadata.avsc");
        
        if (schemaStream == null) {
            // Try loading from file system
            Path schemaPath = Path.of("src/main/avro/table_metadata.avsc");
            if (Files.exists(schemaPath)) {
                schemaStream = Files.newInputStream(schemaPath);
            } else {
                throw new IOException("Schema file not found");
            }
        }
        
        this.schema = new Schema.Parser().parse(schemaStream);
    }
    
    /**
     * Serialize a list of table metadata to Avro file
     */
    public void serializeToFile(List<TableMetadata> tables, Path outputPath) throws IOException {
        // Convert to Avro records
        List<AvroTableMetadata> avroTables = tables.stream()
                .map(this::convertToAvro)
                .collect(Collectors.toList());
        
        // Write to file
        DatumWriter<AvroTableMetadata> datumWriter = new SpecificDatumWriter<>(AvroTableMetadata.class);
        try (DataFileWriter<AvroTableMetadata> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schema, outputPath.toFile());
            
            for (AvroTableMetadata avroTable : avroTables) {
                dataFileWriter.append(avroTable);
            }
        }
    }
    
    /**
     * Serialize to bytes (for size comparison)
     */
    public byte[] serializeToBytes(List<TableMetadata> tables) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        List<AvroTableMetadata> avroTables = tables.stream()
                .map(this::convertToAvro)
                .collect(Collectors.toList());
        
        DatumWriter<AvroTableMetadata> datumWriter = new SpecificDatumWriter<>(AvroTableMetadata.class);
        try (DataFileWriter<AvroTableMetadata> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schema, outputStream);
            
            for (AvroTableMetadata avroTable : avroTables) {
                dataFileWriter.append(avroTable);
            }
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Deserialize table metadata from Avro file
     */
    public List<TableMetadata> deserializeFromFile(Path inputPath) throws IOException {
        List<TableMetadata> tables = new ArrayList<>();
        
        DatumReader<AvroTableMetadata> datumReader = new SpecificDatumReader<>(AvroTableMetadata.class);
        try (DataFileReader<AvroTableMetadata> dataFileReader = 
                new DataFileReader<>(inputPath.toFile(), datumReader)) {
            
            while (dataFileReader.hasNext()) {
                AvroTableMetadata avroTable = dataFileReader.next();
                tables.add(convertFromAvro(avroTable));
            }
        }
        
        return tables;
    }
    
    /**
     * Convert from our model to Avro model
     */
    private AvroTableMetadata convertToAvro(TableMetadata table) {
        return AvroTableMetadata.newBuilder()
                .setTableName(table.getTableName())
                .setDatabaseName(table.getDatabaseName())
                .setSchemaName(table.getSchemaName())
                .setTableType(table.getTableType())
                .setColumns(convertColumns(table.getColumns()))
                .setIndexes(convertIndexes(table.getIndexes()))
                .setForeignKeys(convertForeignKeys(table.getForeignKeys()))
                .setRowCount(table.getRowCount())
                .setTableSizeBytes(table.getTableSizeBytes())
                .setCreatedAt(table.getCreatedAt())
                .setLastModified(table.getLastModified())
                .setTableComment(table.getTableComment())
                .build();
    }
    
    /**
     * Convert from Avro model to our model
     */
    private TableMetadata convertFromAvro(AvroTableMetadata avroTable) {
        return TableMetadata.builder()
                .tableName(avroTable.getTableName().toString())
                .databaseName(avroTable.getDatabaseName().toString())
                .schemaName(avroTable.getSchemaName().toString())
                .tableType(avroTable.getTableType().toString())
                .columns(convertAvroColumns(avroTable.getColumns()))
                .indexes(convertAvroIndexes(avroTable.getIndexes()))
                .foreignKeys(convertAvroForeignKeys(avroTable.getForeignKeys()))
                .rowCount(avroTable.getRowCount())
                .tableSizeBytes(avroTable.getTableSizeBytes())
                .createdAt(avroTable.getCreatedAt() != null ? avroTable.getCreatedAt().toString() : null)
                .lastModified(avroTable.getLastModified() != null ? avroTable.getLastModified().toString() : null)
                .tableComment(avroTable.getTableComment() != null ? avroTable.getTableComment().toString() : null)
                .build();
    }
    
    private List<AvroColumnMetadata> convertColumns(List<ColumnMetadata> columns) {
        return columns.stream()
                .map(col -> AvroColumnMetadata.newBuilder()
                        .setColumnName(col.getColumnName())
                        .setDataType(col.getDataType())
                        .setIsNullable(col.isNullable())
                        .setIsPrimaryKey(col.isPrimaryKey())
                        .setDefaultValue(col.getDefaultValue())
                        .setColumnSize(col.getColumnSize())
                        .setPrecision(col.getPrecision())
                        .setScale(col.getScale())
                        .setColumnComment(col.getColumnComment())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<ColumnMetadata> convertAvroColumns(List<AvroColumnMetadata> avroColumns) {
        return avroColumns.stream()
                .map(col -> ColumnMetadata.builder()
                        .columnName(col.getColumnName().toString())
                        .dataType(col.getDataType().toString())
                        .isNullable(col.getIsNullable())
                        .isPrimaryKey(col.getIsPrimaryKey())
                        .defaultValue(col.getDefaultValue() != null ? col.getDefaultValue().toString() : null)
                        .columnSize(col.getColumnSize())
                        .precision(col.getPrecision())
                        .scale(col.getScale())
                        .columnComment(col.getColumnComment() != null ? col.getColumnComment().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<AvroIndexMetadata> convertIndexes(List<IndexMetadata> indexes) {
        return indexes.stream()
                .map(idx -> AvroIndexMetadata.newBuilder()
                        .setIndexName(idx.getIndexName())
                        .setColumns(idx.getColumns())
                        .setIsUnique(idx.isUnique())
                        .setIndexType(idx.getIndexType())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<IndexMetadata> convertAvroIndexes(List<AvroIndexMetadata> avroIndexes) {
        return avroIndexes.stream()
                .map(idx -> IndexMetadata.builder()
                        .indexName(idx.getIndexName().toString())
                        .columns(idx.getColumns().stream()
                                .map(Object::toString)
                                .collect(Collectors.toList()))
                        .isUnique(idx.getIsUnique())
                        .indexType(idx.getIndexType().toString())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<AvroForeignKeyMetadata> convertForeignKeys(List<ForeignKeyMetadata> foreignKeys) {
        return foreignKeys.stream()
                .map(fk -> AvroForeignKeyMetadata.newBuilder()
                        .setConstraintName(fk.getConstraintName())
                        .setColumnNames(fk.getColumnNames())
                        .setReferencedTable(fk.getReferencedTable())
                        .setReferencedColumns(fk.getReferencedColumns())
                        .setOnDelete(fk.getOnDelete())
                        .setOnUpdate(fk.getOnUpdate())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<ForeignKeyMetadata> convertAvroForeignKeys(List<AvroForeignKeyMetadata> avroForeignKeys) {
        return avroForeignKeys.stream()
                .map(fk -> ForeignKeyMetadata.builder()
                        .constraintName(fk.getConstraintName().toString())
                        .columnNames(fk.getColumnNames().stream()
                                .map(Object::toString)
                                .collect(Collectors.toList()))
                        .referencedTable(fk.getReferencedTable().toString())
                        .referencedColumns(fk.getReferencedColumns().stream()
                                .map(Object::toString)
                                .collect(Collectors.toList()))
                        .onDelete(fk.getOnDelete() != null ? fk.getOnDelete().toString() : null)
                        .onUpdate(fk.getOnUpdate() != null ? fk.getOnUpdate().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Get the size of serialized data in bytes
     */
    public long getSerializedSize(List<TableMetadata> tables) throws IOException {
        return serializeToBytes(tables).length;
    }
}

