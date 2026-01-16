package com.learning.comparison.ddl;

import com.learning.comparison.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generates realistic table metadata from sample DDL statements
 */
public class DDLGenerator {
    
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    
    private static final String[] TABLE_PREFIXES = {
        "user", "order", "product", "customer", "invoice", "payment",
        "transaction", "account", "employee", "department", "inventory",
        "shipment", "supplier", "category", "review", "audit"
    };
    
    private static final String[] DATA_TYPES = {
        "BIGINT", "VARCHAR", "INTEGER", "DECIMAL", "TIMESTAMP",
        "DATE", "BOOLEAN", "TEXT", "DOUBLE", "CHAR"
    };
    
    /**
     * Generate multiple table metadata objects
     */
    public static List<TableMetadata> generateTableMetadata(int count) {
        List<TableMetadata> tables = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            tables.add(generateSingleTable(i));
        }
        
        return tables;
    }
    
    /**
     * Generate a single realistic table metadata
     */
    private static TableMetadata generateSingleTable(int index) {
        String tablePrefix = TABLE_PREFIXES[index % TABLE_PREFIXES.length];
        String tableName = tablePrefix + "_" + (index / TABLE_PREFIXES.length);
        
        int columnCount = 5 + random.nextInt(15); // 5-20 columns
        int indexCount = 1 + random.nextInt(3);   // 1-3 indexes
        int fkCount = random.nextInt(3);          // 0-2 foreign keys
        
        return TableMetadata.builder()
                .tableName(tableName)
                .databaseName("production_db")
                .schemaName("public")
                .tableType("TABLE")
                .columns(generateColumns(columnCount, tableName))
                .indexes(generateIndexes(indexCount, columnCount))
                .foreignKeys(generateForeignKeys(fkCount))
                .rowCount((long) (10000 + random.nextInt(990000)))
                .tableSizeBytes((long) (1048576 + random.nextInt(104857600))) // 1MB-100MB
                .createdAt(LocalDateTime.now().minusDays(random.nextInt(365)).format(formatter))
                .lastModified(LocalDateTime.now().minusDays(random.nextInt(30)).format(formatter))
                .tableComment("Table for " + tablePrefix + " data")
                .build();
    }
    
    /**
     * Generate column metadata
     */
    private static List<ColumnMetadata> generateColumns(int count, String tableName) {
        List<ColumnMetadata> columns = new ArrayList<>();
        
        // Always add an ID column as primary key
        columns.add(ColumnMetadata.builder()
                .columnName("id")
                .dataType("BIGINT")
                .isNullable(false)
                .isPrimaryKey(true)
                .defaultValue(null)
                .columnSize(20)
                .precision(null)
                .scale(null)
                .columnComment("Primary key")
                .build());
        
        // Generate other columns
        for (int i = 1; i < count; i++) {
            String dataType = DATA_TYPES[random.nextInt(DATA_TYPES.length)];
            
            columns.add(ColumnMetadata.builder()
                    .columnName("column_" + i)
                    .dataType(dataType)
                    .isNullable(random.nextBoolean())
                    .isPrimaryKey(false)
                    .defaultValue(random.nextBoolean() ? null : getDefaultValue(dataType))
                    .columnSize(getColumnSize(dataType))
                    .precision(dataType.equals("DECIMAL") ? 10 : null)
                    .scale(dataType.equals("DECIMAL") ? 2 : null)
                    .columnComment("Column " + i + " comment")
                    .build());
        }
        
        return columns;
    }
    
    /**
     * Generate index metadata
     */
    private static List<IndexMetadata> generateIndexes(int count, int maxColumns) {
        List<IndexMetadata> indexes = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            int indexColumnCount = 1 + random.nextInt(Math.min(3, maxColumns));
            List<String> indexColumns = new ArrayList<>();
            
            for (int j = 0; j < indexColumnCount; j++) {
                indexColumns.add("column_" + random.nextInt(maxColumns));
            }
            
            indexes.add(IndexMetadata.builder()
                    .indexName("idx_" + i)
                    .columns(indexColumns)
                    .isUnique(random.nextBoolean())
                    .indexType(random.nextBoolean() ? "BTREE" : "HASH")
                    .build());
        }
        
        return indexes;
    }
    
    /**
     * Generate foreign key metadata
     */
    private static List<ForeignKeyMetadata> generateForeignKeys(int count) {
        List<ForeignKeyMetadata> foreignKeys = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            foreignKeys.add(ForeignKeyMetadata.builder()
                    .constraintName("fk_" + i)
                    .columnNames(Arrays.asList("column_" + random.nextInt(10)))
                    .referencedTable("ref_table_" + i)
                    .referencedColumns(Arrays.asList("id"))
                    .onDelete(random.nextBoolean() ? "CASCADE" : "SET NULL")
                    .onUpdate("NO ACTION")
                    .build());
        }
        
        return foreignKeys;
    }
    
    private static String getDefaultValue(String dataType) {
        switch (dataType) {
            case "BIGINT":
            case "INTEGER":
                return "0";
            case "VARCHAR":
            case "TEXT":
            case "CHAR":
                return "''";
            case "BOOLEAN":
                return "false";
            case "TIMESTAMP":
            case "DATE":
                return "CURRENT_TIMESTAMP";
            case "DECIMAL":
            case "DOUBLE":
                return "0.0";
            default:
                return null;
        }
    }
    
    private static Integer getColumnSize(String dataType) {
        switch (dataType) {
            case "VARCHAR":
                return 255;
            case "CHAR":
                return 50;
            case "TEXT":
                return 65535;
            case "BIGINT":
                return 20;
            case "INTEGER":
                return 11;
            default:
                return null;
        }
    }
    
    /**
     * Generate a sample DDL statement from metadata (for demonstration)
     */
    public static String generateDDL(TableMetadata metadata) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE ").append(metadata.getSchemaName())
           .append(".").append(metadata.getTableName()).append(" (\n");
        
        for (int i = 0; i < metadata.getColumns().size(); i++) {
            ColumnMetadata col = metadata.getColumns().get(i);
            ddl.append("  ").append(col.getColumnName())
               .append(" ").append(col.getDataType());
            
            if (col.getColumnSize() != null) {
                ddl.append("(").append(col.getColumnSize()).append(")");
            }
            
            if (!col.isNullable()) {
                ddl.append(" NOT NULL");
            }
            
            if (col.getDefaultValue() != null) {
                ddl.append(" DEFAULT ").append(col.getDefaultValue());
            }
            
            if (i < metadata.getColumns().size() - 1) {
                ddl.append(",");
            }
            ddl.append("\n");
        }
        
        ddl.append(");\n");
        
        return ddl.toString();
    }
}

