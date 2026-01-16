# Quick Start Guide

## What This Project Does

Demonstrates **why migrating from JSON to Avro improves performance** for storing database metadata:

- 📉 **80% smaller files**
- ⚡ **1.7x faster reads**
- ✅ **Schema validation**
- 🔄 **Backward compatibility**

## Run It Now

```bash
# Option 1: Using Gradle
./gradlew run

# Option 2: Using the script
./run_comparison.sh

# Option 3: Custom table count
./gradlew run --args="100000"
```

## Expected Output

```
================================================================================
JSON vs AVRO PERFORMANCE COMPARISON
================================================================================

📊 File Size:
  JSON:  384.01 MB
  Avro:  74.67 MB
  ✅ Avro is 80.55% smaller

⚡ Read Performance:
  JSON:  2347 ms (42607.58 records/sec)
  Avro:  1415 ms (70671.38 records/sec)
  ✅ Avro is 1.66x faster
```

## Files Generated

```
output/
├── metadata.json  (385 MB)
└── metadata.avro  (75 MB)
```

## View the Data

```bash
# JSON (human-readable)
head -c 2000 output/metadata.json

# Avro schema
cat src/main/avro/table_metadata.avsc

# File sizes
ls -lh output/
```

## Interview Talking Points

### Question: "How did you improve metadata storage performance?"

**Answer**:
> "I migrated our metadata storage from JSON to Apache Avro, which reduced file sizes by 80% and improved read performance by 1.7x. 
>
> The key was eliminating JSON's overhead - field names are repeated in every record in JSON, but in Avro they're stored once in the schema. Combined with binary encoding, this gave us significant performance gains when loading 100,000+ table definitions."

### Question: "Why Avro instead of Protobuf or other formats?"

**Answer**:
> "Avro was the best fit because:
> 1. **Schema Evolution**: Avro supports adding/removing fields without breaking compatibility
> 2. **Dynamic Typing**: No code generation required (though we can use it for better type safety)
> 3. **Self-Describing**: Schema is embedded in the file, making it portable
> 4. **Big Data Ecosystem**: Well-integrated with Spark, Kafka, Hadoop"

### Question: "What were the challenges?"

**Answer**:
> "Main challenges were:
> 1. **Schema Management**: Had to design a stable schema that could evolve
> 2. **Team Training**: Not everyone was familiar with Avro
> 3. **Tooling**: JSON is human-readable, Avro needs special tools to inspect
> 4. **Migration**: Had to support both formats during transition
>
> But the performance gains (80% size reduction) made it worth it."

## Project Structure

```
json-avro-comparison/
├── src/main/java/com/learning/comparison/
│   ├── PerformanceComparisonApp.java    ← Main entry point
│   ├── model/                           ← Data models
│   ├── ddl/DDLGenerator.java            ← Generate test data
│   ├── json/JsonSerializer.java         ← JSON handling
│   ├── avro/AvroSerializer.java         ← Avro handling
│   └── benchmark/Benchmark.java         ← Performance testing
│
├── src/main/avro/
│   └── table_metadata.avsc              ← Avro schema
│
├── output/
│   ├── metadata.json                    ← Generated JSON
│   └── metadata.avro                    ← Generated Avro
│
├── README.md                            ← Overview
├── DETAILED_GUIDE.md                    ← In-depth explanation
├── EXAMPLE_DATA.md                      ← Data examples
└── QUICK_START.md                       ← This file
```

## Next Steps

1. **Run the comparison** with different table counts
2. **Read DETAILED_GUIDE.md** for technical deep dive
3. **Check EXAMPLE_DATA.md** for visual comparisons
4. **Experiment** with the code:
   - Disable JSON pretty printing → even smaller
   - Add Avro compression → 50-60% smaller
   - Increase table count → see scalability

## Customize It

### Change Table Count
```bash
./gradlew run --args="50000"
```

### Modify Data Structure
Edit `src/main/java/com/learning/comparison/ddl/DDLGenerator.java`:
- Change column count
- Add more indexes
- Include partitioning info

### Update Schema
Edit `src/main/avro/table_metadata.avsc`:
- Add new fields
- Change field types
- Then rebuild: `./gradlew build`

## Troubleshooting

### Build Fails
```bash
./gradlew clean build --refresh-dependencies
```

### Avro Classes Not Generated
```bash
./gradlew generateAvroJava
```

### Out of Memory
```bash
# For very large table counts (1M+)
export GRADLE_OPTS="-Xmx4g"
./gradlew run --args="1000000"
```

## Resources

- [Apache Avro Documentation](https://avro.apache.org/docs/)
- [Avro vs JSON Performance](https://www.confluent.io/blog/avro-kafka-data/)
- [Schema Evolution Best Practices](https://docs.confluent.io/platform/current/schema-registry/avro.html)

---

**Built with**: Java 17, Gradle 8.13, Apache Avro 1.11.3, Jackson 2.16.0

