# Folder-Based Data Organization

## Quick Summary

This project now supports **two data organization patterns**:

### 1. Single-File (Original)
```bash
./gradlew run --args="10000"
```
Output: `output/metadata.json` (one file with everything)

### 2. Folder-Based (New!)
```bash
./gradlew runFolderComparison -Pargs="10000"
```
Output:
```
output/metadata_folders/
├── tables/metadata.json
├── columns/metadata.json
├── indexes/metadata.json
└── foreign_keys/metadata.json
```

## Key Differences

| Aspect | Single-File | Folder-Based |
|--------|------------|--------------|
| **Files** | 1 | 4 |
| **Size** | 38 MB | 49 MB (+28%) |
| **Best For** | Complete loads | Selective queries |
| **Pattern** | Monolithic | Data Lake |

## When to Use Folder-Based

✅ **Use folder-based when you need**:
- Query specific entity types independently
- Parallel processing of different entities
- Data lake/warehouse architecture
- Different access patterns per entity type
- Independent schema evolution

❌ **Don't use folder-based when you**:
- Always need complete table information
- Value simplicity over flexibility
- Have small datasets (<100 MB)
- Need guaranteed referential integrity

## Example Queries

### Folder-Based Advantage
```bash
# Query: "Find all VARCHAR columns"
# Single-file: Load 38 MB
# Folder-based: Load only columns/ (more focused)

# Query: "List all indexes"
# Single-file: Load 38 MB
# Folder-based: Load only indexes/ (4.2 MB)
```

### Single-File Advantage
```bash
# Query: "Get complete schema for table 'users'"
# Single-file: One lookup, everything included
# Folder-based: Read 4 files and join in memory
```

## Performance Results (10,000 Tables)

### File Sizes
- Single-file: **38.34 MB**
- Folder-based: **49.05 MB** (28% larger)
  - tables/: 3.2 MB
  - columns/: 39 MB (largest - most denormalized)
  - indexes/: 4.2 MB
  - foreign_keys/: 2.9 MB

### Write Time
- Single-file: **310 ms**
- Folder-based: **326 ms** (5% slower)

### Read Time (Complete Reconstruction)
- Single-file: **366 ms** ✅
- Folder-based: **127,566 ms** (348x slower due to joins)

**Note**: Folder-based isn't designed for complete reconstruction! It's optimized for selective queries.

## Real-World Use Cases

### Data Catalog (Folder-Based ✅)
```python
# Users query metadata independently
columns = load("columns/")
date_columns = filter_by_type(columns, 'DATE')
```

### Migration Tool (Single-File ✅)
```java
// Need complete schema for each table
tables = loadMetadata();
for (table : tables) {
    migrate(table); // needs columns, indexes, FKs
}
```

### Analytics Pipeline (Folder-Based ✅)
```sql
-- Load only what you need
SELECT COUNT(*) FROM columns WHERE data_type = 'BIGINT'
```

## Running Comparisons

### Compare JSON Single vs Folder
```bash
./gradlew runFolderComparison -Pargs="10000"
```

### Compare JSON vs Avro (Original)
```bash
./gradlew run --args="10000"
```

### All Formats Comparison
```bash
# Run both and compare
./gradlew run --args="10000"
./gradlew runFolderComparison -Pargs="10000"
```

## Folder Structure Created

After running `runFolderComparison`:

```
output/
├── metadata_single.json          # 38 MB - one file
└── metadata_folders/             # 49 MB total - separated
    ├── tables/
    │   └── metadata.json         # 3.2 MB - table info
    ├── columns/
    │   └── metadata.json         # 39 MB - all columns
    ├── indexes/
    │   └── metadata.json         # 4.2 MB - all indexes
    └── foreign_keys/
        └── metadata.json         # 2.9 MB - all FKs
```

## Data Lake Pattern

Folder-based organization follows the **data lake pattern**:

```
bronze/         # Raw DDL
  └── ddl_statements.sql

silver/         # Parsed, cleaned
  ├── tables/
  ├── columns/
  ├── indexes/
  └── foreign_keys/

gold/           # Analytics-ready
  └── dimensional_model/
```

Benefits:
- ✅ Process entities independently
- ✅ Different retention policies per entity
- ✅ Easier incremental updates
- ✅ Better for distributed processing (Spark, etc.)

## Trade-offs

### Folder-Based
**Pros**:
- Selective access
- Parallel processing
- Independent evolution
- Analytics-friendly

**Cons**:
- 28% larger (denormalization overhead)
- Complex joins for reconstruction
- More I/O operations
- No built-in referential integrity

### Single-File
**Pros**:
- Compact
- Simple
- Fast full loads
- Referential integrity

**Cons**:
- Must load everything
- No selective access
- Rigid schema
- Sequential processing

## Conclusion

Both patterns have their place:

- **Single-file**: Migration, backup, simple use cases
- **Folder-based**: Analytics, data lakes, flexible queries
- **Best solution**: Maintain both!

```bash
# Generate both formats
./gradlew run --args="10000"              # Single-file
./gradlew runFolderComparison -Pargs="10000"  # Folder-based
```

---

For detailed analysis, see [FOLDER_ORGANIZATION_GUIDE.md](FOLDER_ORGANIZATION_GUIDE.md)

