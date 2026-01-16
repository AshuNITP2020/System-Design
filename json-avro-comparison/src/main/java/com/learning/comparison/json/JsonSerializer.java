package com.learning.comparison.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.learning.comparison.model.TableMetadata;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles JSON serialization and deserialization of table metadata
 */
public class JsonSerializer {
    
    private final ObjectMapper objectMapper;
    
    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
        // Pretty print for readability (disable for even smaller size)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Serialize a list of table metadata to JSON file
     */
    public void serializeToFile(List<TableMetadata> tables, Path outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            // Write as JSON array
            objectMapper.writeValue(writer, tables);
        }
    }
    
    /**
     * Serialize a single table to JSON string
     */
    public String serializeToString(TableMetadata table) throws IOException {
        return objectMapper.writeValueAsString(table);
    }
    
    /**
     * Serialize a list of tables to JSON string
     */
    public String serializeListToString(List<TableMetadata> tables) throws IOException {
        return objectMapper.writeValueAsString(tables);
    }
    
    /**
     * Deserialize table metadata from JSON file
     */
    public List<TableMetadata> deserializeFromFile(Path inputPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            return objectMapper.readValue(reader, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, TableMetadata.class));
        }
    }
    
    /**
     * Deserialize a single table from JSON string
     */
    public TableMetadata deserializeFromString(String json) throws IOException {
        return objectMapper.readValue(json, TableMetadata.class);
    }
    
    /**
     * Get the size of serialized data in bytes
     */
    public long getSerializedSize(List<TableMetadata> tables) throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(tables);
        return bytes.length;
    }
}

