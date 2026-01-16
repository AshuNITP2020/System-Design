# Example Data and Visual Comparison

## Sample DDL Statement

The application generates realistic table metadata from DDL statements like:

```sql
CREATE TABLE public.users (
  id BIGINT(20) NOT NULL,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(320) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_active BOOLEAN DEFAULT true,
  last_login TIMESTAMP,
  profile_data TEXT
);

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
ALTER TABLE users ADD CONSTRAINT fk_department 
  FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE;
```

---

## JSON Representation

**Size**: ~4,000 bytes per table

```json
{
  "table_name": "users",
  "database_name": "production_db",
  "schema_name": "public",
  "table_type": "TABLE",
  "columns": [
    {
      "column_name": "id",
      "data_type": "BIGINT",
      "is_nullable": false,
      "is_primary_key": true,
      "default_value": null,
      "column_size": 20,
      "precision": null,
      "scale": null,
      "column_comment": "Primary key"
    },
    {
      "column_name": "username",
      "data_type": "VARCHAR",
      "is_nullable": false,
      "is_primary_key": false,
      "default_value": null,
      "column_size": 255,
      "precision": null,
      "scale": null,
      "column_comment": "Username field"
    },
    {
      "column_name": "email",
      "data_type": "VARCHAR",
      "is_nullable": false,
      "is_primary_key": false,
      "default_value": null,
      "column_size": 320,
      "precision": null,
      "scale": null,
      "column_comment": "Email address"
    }
  ],
  "indexes": [
    {
      "index_name": "idx_username",
      "columns": ["username"],
      "is_unique": true,
      "index_type": "BTREE"
    }
  ],
  "foreign_keys": [],
  "row_count": 125000,
  "table_size_bytes": 52428800,
  "created_at": "2025-03-15T10:30:00",
  "last_modified": "2026-01-10T15:45:00",
  "table_comment": "User accounts table"
}
```

**Issues**:
- 🔴 Field names repeated: `"column_name"`, `"data_type"`, etc.
- 🔴 Null values: `"precision": null` (11 bytes)
- 🔴 Boolean as text: `"is_nullable": false` (23 bytes)
- 🔴 Syntax overhead: `{`, `}`, `:`, `,`, `"`
- 🔴 Whitespace: Pretty printing adds ~30% size

---

## Avro Binary Representation

**Size**: ~800 bytes per table (80% smaller!)

### Schema (Stored Once)
```json
{
  "type": "record",
  "name": "AvroTableMetadata",
  "fields": [
    {"name": "tableName", "type": "string"},
    {"name": "databaseName", "type": "string"},
    {"name": "columns", "type": {
      "type": "array",
      "items": {
        "type": "record",
        "name": "AvroColumnMetadata",
        "fields": [
          {"name": "columnName", "type": "string"},
          {"name": "dataType", "type": "string"},
          {"name": "isNullable", "type": "boolean"}
        ]
      }
    }}
  ]
}
```

### Binary Data (Conceptual Representation)
```
Header: [Magic: 0x4F 0x62 0x6A 0x01]
Schema: [schema_json]

Record 1:
  tableName: [0x05 0x75 0x73 0x65 0x72 0x73]  // "users"
  databaseName: [0x0D 0x70 0x72 0x6F 0x64 0x75 0x63 0x74 0x69 0x6F 0x6E 0x5F 0x64 0x62]
  columns: [0x06]  // array length: 3
    column[0]:
      columnName: [0x02 0x69 0x64]  // "id"
      dataType: [0x0C 0x42 0x49 0x47 0x49 0x4E 0x54]  // "BIGINT"
      isNullable: [0x00]  // false (1 byte!)
    ...
```

**Benefits**:
- ✅ No field names in data (schema has them)
- ✅ Null values: 1 byte
- ✅ Boolean: 1 byte (vs 4-5 bytes in JSON)
- ✅ No syntax overhead
- ✅ Variable-length integer encoding
- ✅ Direct binary read (no parsing)

---

## Size Breakdown

### JSON (4,000 bytes per table)
```
Field names:      ~1,200 bytes  (30%)
Field values:     ~1,500 bytes  (37.5%)
Syntax ({}:,"):   ~800 bytes    (20%)
Whitespace:       ~500 bytes    (12.5%)
─────────────────────────────────────
Total:            ~4,000 bytes
```

### Avro (800 bytes per table)
```
Schema: 0 bytes (stored once, shared across all records)
Field values:     ~750 bytes  (93.75%)
Block metadata:   ~50 bytes   (6.25%)
─────────────────────────────────────
Total:            ~800 bytes
```

---

## Performance Comparison Visual

### File Size for 100,000 Tables

```
JSON:  ████████████████████████████████████████  385 MB
Avro:  ████████                                    75 MB
       
       ↓ 80.55% reduction
```

### Read Time for 100,000 Tables

```
JSON:  ████████████████████  2,347 ms
Avro:  ████████████          1,415 ms

       ↓ 1.66x faster
```

### Write Time for 100,000 Tables

```
JSON:  ████████████████████  1,886 ms
Avro:  ███████████████████   1,826 ms

       ↓ 1.03x faster (similar)
```

### Throughput (Records/Second)

```
JSON Read:   ██████████████████████        42,607 records/sec
Avro Read:   ████████████████████████████  70,671 records/sec
             
             ↓ 66% faster throughput
```

---

## Byte-Level Comparison

### JSON: Boolean Field
```
"is_nullable": false
│    │       │  │   │
│    │       │  │   └─ Value: 5 bytes
│    │       │  └───── Colon: 1 byte
│    │       └──────── Space: 1 byte
│    └──────────────── Field name: 14 bytes
└───────────────────── Quotes: 2 bytes

Total: 23 bytes
```

### Avro: Boolean Field
```
0x00
│
└─ Value: 1 byte (0x00 = false, 0x01 = true)

Total: 1 byte (field name in schema, not data)
```

**Savings**: 23 bytes → 1 byte (96% reduction!)

---

## String Encoding Comparison

### JSON: String Field
```
"column_name": "user_id"
│    │       │  │      │
│    │       │  │      └─ Value: 7 bytes
│    │       │  └──────── Quotes: 2 bytes
│    │       └─────────── Colon + space: 2 bytes
│    └─────────────────── Field name: 14 bytes
└──────────────────────── Quotes: 2 bytes

Total: 27 bytes
```

### Avro: String Field
```
0x07 u s e r _ i d
│    │
│    └─ Value: 7 bytes (UTF-8)
└────── Length prefix: 1 byte

Total: 8 bytes (field name in schema)
```

**Savings**: 27 bytes → 8 bytes (70% reduction!)

---

## Real-World Impact

### Scenario: 100,000 Tables

#### Storage Costs (AWS S3)
```
JSON:  385 MB × $0.023/GB = $0.009/month
Avro:   75 MB × $0.023/GB = $0.002/month

Annual savings: $0.084 (for 100K tables)
For 10M tables: $84/year
```

#### Network Transfer (10 Gbps)
```
JSON:  385 MB ÷ (10 Gbps / 8) = 0.308 seconds
Avro:   75 MB ÷ (10 Gbps / 8) = 0.060 seconds

Time saved per transfer: 0.248 seconds
For 1000 transfers/day: 248 seconds = 4 minutes saved/day
```

#### Application Startup Time
```
JSON:  2,347 ms to load metadata
Avro:  1,415 ms to load metadata

Time saved per startup: 932 ms
For 100 restarts/day: 93.2 seconds = 1.5 minutes saved/day
```

---

## Key Takeaways

1. **Size**: Avro eliminates field name overhead → 80% smaller files
2. **Speed**: Binary format enables direct deserialization → 1.7x faster reads
3. **Schema**: Compile-time validation → fewer runtime errors
4. **Evolution**: Add/remove fields without breaking compatibility
5. **Production**: Real cost and time savings at scale

---

## Try It Yourself

```bash
# Run comparison
./gradlew run --args="10000"

# View JSON output (first 2KB)
head -c 2000 output/metadata.json

# Check file sizes
ls -lh output/

# View Avro schema
cat src/main/avro/table_metadata.avsc
```

