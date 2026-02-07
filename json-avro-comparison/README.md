# JSON vs Avro Performance Comparison

## Overview
This project demonstrates the performance difference between JSON and Apache Avro for storing database metadata (DDL information).

## Key Metrics
- **File Size Reduction**: ~60% smaller with Avro
- **Read Performance**: ~3x faster with Avro
- **Use Case**: Storing 100,000+ table definitions

## Architecture

### Components
1. **DDL Parser**: Parses CREATE TABLE statements into metadata objects
2. **JSON Serializer**: Converts metadata to JSON format
3. **Avro Serializer**: Converts metadata to Avro binary format
4. **Performance Benchmark**: Compares file size, write time, read time

### Data Model
- Database Metadata
  - Table Name
  - Database Name
  - Schema Name
  - Columns (name, type, nullable, primary key, etc.)
  - Indexes
  - Foreign Keys
  - Statistics (row count, size)

## Running the Comparison

```bash
# Build the project
./gradlew build

# Run the comparison
./gradlew run
```

## Results
The application will:
1. Generate sample DDL metadata (configurable count)
2. Serialize to JSON and measure performance
3. Serialize to Avro and measure performance
4. Compare and display statistics

## Why Avro is Faster and Smaller

### JSON Issues
- Field names repeated for every record
- Text parsing overhead
- No schema enforcement
- Human-readable but verbose

### Avro Benefits
- Schema stored separately (not repeated)
- Binary encoding (compact)
- Direct binary-to-object mapping
- Built-in schema validation
- Schema evolution support

