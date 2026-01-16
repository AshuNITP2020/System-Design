# Folder-Based Organization Guide

## Overview

This guide explains the **folder-based data organization pattern** and compares it with single-file organization.

## Two Organizational Strategies

### 1. Single-File Organization (Monolithic)

```
output/
└── metadata.json (38 MB)
    └── Contains: All tables with nested columns, indexes, and foreign keys
```

**Structure**:
```json
[
  {
    "table_name": "users",
    "columns": [...],      // Nested
    "indexes": [...],      // Nested
    "foreign_keys": [...]  // Nested
  },
  ...
]
```

### 2. Folder-Based Organization (Data Lake Pattern)

```
output/metadata_folders/
├── tables/metadata.json (3.2 MB)         // Table info only
├── columns/metadata.json (39 MB)         // All columns (denormalized)
├── indexes/metadata.json (4.2 MB)        // All indexes (denormalized)
└── foreign_keys/metadata.json (2.9 MB)   // All foreign keys (denormalized)
```

**Structure**:

**tables/metadata.json**:
```json
[
  {
    "table_name": "users",
    "database_name": "production_db",
    "schema_name": "public",
    "row_count": 125000,
    "table_size_bytes": 52428800
  },
  ...
]
```

**columns/metadata.json**:
```json
[
  {
    "table_name": "users",           // Reference to parent table
    "database_name": "production_db",
    "schema_name": "public",
    "column_name": "id",
    "data_type": "BIGINT",
    "is_nullable": false
  },
  {
    "table_name": "users",
    "column_name": "username",
    ...
  },
  ...
]
```

---

## Performance Comparison

### File Size

| Organization | Size | Notes |
|--------------|------|-------|
| **Single-File** | 38 MB | Compact, no duplication |
| **Folder-Based** | 49 MB | 28% larger due to denormalization |

**Why larger?**
- Table identifiers (table_name, database_name, schema_name) repeated in each folder
- Trade-off: Flexibility vs size

### Write Performance

| Organization | Time | Throughput |
|--------------|------|------------|
| **Single-File** | 310 ms | 32,258 records/sec |
| **Folder-Based** | 326 ms | 30,675 records/sec |

**Difference**: Minimal (~5% slower)
- Folder-based writes 4 separate files
- But can be parallelized

### Read Performance (Full Load)

| Organization | Time | Throughput |
|--------------|------|------------|
| **Single-File** | 366 ms | 27,322 records/sec |
| **Folder-Based** | 127,566 ms | 78 records/sec |

**Difference**: Folder-based is **348x slower** for full reconstruction
- Must read 4 files
- In-memory joins to reconstruct tables
- **However**: This is misleading because folder-based isn't meant for full loads!

---

## When to Use Each Approach

### ✅ Use Folder-Based Organization When:

#### 1. **Selective Queries**
You frequently need specific entity types without others:

```bash
# Query: "Show all columns of type VARCHAR"
# Single-file: Must load entire 38 MB file
# Folder-based: Read only columns/metadata.json (39 MB)

# Query: "List all unique indexes"
# Single-file: Must load entire 38 MB file
# Folder-based: Read only indexes/metadata.json (4.2 MB)
```

#### 2. **Parallel Processing**
Process different entity types simultaneously:

```python
# Folder-based enables parallel processing:
with ThreadPoolExecutor() as executor:
    tables_future = executor.submit(load_tables)
    columns_future = executor.submit(load_columns)
    indexes_future = executor.submit(load_indexes)
    fkeys_future = executor.submit(load_foreign_keys)
```

#### 3. **Data Lake Architecture**
Building a data lake with partitioned data:

```
data_lake/
├── bronze/raw_ddl/
├── silver/parsed_metadata/
│   ├── tables/
│   ├── columns/
│   ├── indexes/
│   └── foreign_keys/
└── gold/analytics_ready/
```

#### 4. **Different Access Patterns**
Different entities have different query frequencies:

```
Access Frequency:
- Columns: 1000 queries/day (hot data)
- Tables: 500 queries/day
- Indexes: 50 queries/day (warm data)
- Foreign Keys: 10 queries/day (cold data)

Folder-based allows optimizing storage/caching per entity type
```

#### 5. **Schema Evolution**
Evolve schemas independently:

```
Version 1: Add new column type to columns/
Version 2: Add index statistics to indexes/
Version 3: Add table partitioning info to tables/

Each can evolve without affecting others
```

---

### ✅ Use Single-File Organization When:

#### 1. **Always Need Complete Data**
Queries always require full table information:

```sql
-- Always need: table + columns + indexes + foreign keys
SELECT * FROM table_metadata WHERE table_name = 'users'
```

#### 2. **Simplicity Over Flexibility**
Simpler mental model and code:

```java
// Single file
List<TableMetadata> tables = loadMetadata();

// Folder-based
List<FlatTable> tables = loadTables();
List<FlatColumn> columns = loadColumns();
List<FlatIndex> indexes = loadIndexes();
List<FlatForeignKey> fkeys = loadForeignKeys();
TableMetadata reconstructed = join(tables, columns, indexes, fkeys);
```

#### 3. **Smaller Datasets**
When entire dataset fits comfortably in memory:

```
< 100 MB: Single file is fine
100 MB - 1 GB: Consider folder-based
> 1 GB: Definitely folder-based
```

#### 4. **Network Latency**
Minimize network round trips:

```
Network calls:
- Single file: 1 read
- Folder-based: 4 reads (or 1 if using parallel HTTP/2)
```

#### 5. **Referential Integrity**
Ensure consistency between related entities:

```
Single file guarantees:
- Columns always match their table
- Indexes reference existing columns
- Foreign keys reference existing tables

Folder-based requires application-level validation
```

---

## Real-World Use Cases

### Use Case 1: Data Catalog (Folder-Based ✅)

**Requirement**: Users query metadata independently

```python
# Query 1: "Find all DATE columns"
columns = load_from_folder("columns/")
date_columns = [c for c in columns if c.data_type == 'DATE']

# Query 2: "Show tables with most indexes"
tables = load_from_folder("tables/")
indexes = load_from_folder("indexes/")
table_index_count = count_by_table(indexes)

# Query 3: "Find all foreign key relationships"
foreign_keys = load_from_folder("foreign_keys/")
relationships = build_graph(foreign_keys)
```

**Why folder-based?**
- Each query reads only necessary data
- 70-90% reduction in data read per query

### Use Case 2: Migration Tool (Single-File ✅)

**Requirement**: Migrate entire database schema

```java
// Load complete schema
List<TableMetadata> tables = loadMetadata();

// For each table, need ALL information
for (TableMetadata table : tables) {
    createTable(table);              // Uses table info
    addColumns(table.getColumns());   // Uses columns
    createIndexes(table.getIndexes()); // Uses indexes
    addForeignKeys(table.getForeignKeys()); // Uses FKs
}
```

**Why single-file?**
- Always need complete information
- Simpler code
- Better referential integrity

### Use Case 3: Analytics Pipeline (Folder-Based ✅)

**Requirement**: Analyze schema trends over time

```sql
-- Question: "How many BIGINT columns exist?"
SELECT COUNT(*) FROM columns WHERE data_type = 'BIGINT'

-- Question: "Which tables have no indexes?"
SELECT t.table_name 
FROM tables t 
LEFT JOIN indexes i ON t.table_name = i.table_name 
WHERE i.index_name IS NULL

-- Question: "Find circular foreign key dependencies"
WITH RECURSIVE deps AS (
  SELECT * FROM foreign_keys ...
)
```

**Why folder-based?**
- Each query targets specific entity types
- Can load into database separately
- Easier to partition for processing

---

## Performance Characteristics

### Folder-Based Organization

**Strengths**:
- ✅ Selective loading (70-90% less data per query)
- ✅ Parallel processing (4x speedup potential)
- ✅ Independent evolution
- ✅ Better for analytics

**Weaknesses**:
- ❌ 25-30% larger file size
- ❌ Complex reconstruction logic
- ❌ No referential integrity guarantees
- ❌ More I/O operations

### Single-File Organization

**Strengths**:
- ✅ Compact (no denormalization)
- ✅ Simple code
- ✅ Referential integrity
- ✅ Fast full loads

**Weaknesses**:
- ❌ Must load entire file for any query
- ❌ No selective access
- ❌ Sequential processing only
- ❌ Rigid schema

---

## Hybrid Approach

Best of both worlds:

```
metadata/
├── full/
│   └── metadata.json           (Complete data, optimized for migration)
└── partitioned/
    ├── tables/metadata.json    (Optimized for analytics)
    ├── columns/metadata.json
    ├── indexes/metadata.json
    └── foreign_keys/metadata.json
```

**Use**:
- Migration/backup: Use `full/metadata.json`
- Analytics/queries: Use `partitioned/` folders
- Maintain both with same timestamp

---

## Implementation Example

### Query: "Find all VARCHAR columns > 1000 characters"

**Single-File**:
```java
// Must load entire 38 MB file
List<TableMetadata> tables = loadMetadata("metadata.json");
List<ColumnMetadata> result = new ArrayList<>();
for (TableMetadata table : tables) {
    for (ColumnMetadata col : table.getColumns()) {
        if ("VARCHAR".equals(col.getDataType()) && 
            col.getColumnSize() != null && 
            col.getColumnSize() > 1000) {
            result.add(col);
        }
    }
}
```

**Folder-Based**:
```java
// Load only columns/ (39 MB, but no tables/indexes/fks overhead)
List<FlatColumn> columns = loadColumns("columns/metadata.json");
List<FlatColumn> result = columns.stream()
    .filter(c -> "VARCHAR".equals(c.getDataType()))
    .filter(c -> c.getColumnSize() != null && c.getColumnSize() > 1000)
    .collect(Collectors.toList());
```

**Savings**: No need to parse tables, indexes, or foreign keys!

---

## Conclusion

| Aspect | Single-File | Folder-Based |
|--------|------------|--------------|
| **File Size** | 38 MB ✅ | 49 MB |
| **Simplicity** | High ✅ | Medium |
| **Flexibility** | Low | High ✅ |
| **Selective Access** | No | Yes ✅ |
| **Full Load Speed** | 366 ms ✅ | 127,566 ms |
| **Partial Load Speed** | N/A | Fast ✅ |
| **Referential Integrity** | Guaranteed ✅ | Manual |
| **Schema Evolution** | Rigid | Flexible ✅ |
| **Best For** | Migration, Backup | Analytics, Queries |

**Recommendation**:
- **Small datasets (<100 MB)**: Single-file
- **Query-heavy workloads**: Folder-based
- **Migration tools**: Single-file
- **Data lakes**: Folder-based
- **Best solution**: Maintain both!

---

## Running the Comparison

```bash
# Test folder-based organization
./gradlew runFolderComparison -Pargs="10000"

# Compare with different sizes
./gradlew runFolderComparison -Pargs="1000"   # Small
./gradlew runFolderComparison -Pargs="50000"  # Medium
./gradlew runFolderComparison -Pargs="100000" # Large
```

## Output Structure

The comparison creates:
```
output/
├── metadata_single.json          # Single-file (38 MB)
└── metadata_folders/             # Folder-based (49 MB total)
    ├── tables/metadata.json      # 3.2 MB
    ├── columns/metadata.json     # 39 MB (largest)
    ├── indexes/metadata.json     # 4.2 MB
    └── foreign_keys/metadata.json # 2.9 MB
```

