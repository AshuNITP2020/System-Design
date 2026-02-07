#!/bin/bash

# Run Folder-Based vs Single-File Comparison
# Usage: ./run_folder_comparison.sh [table_count]

set -e

TABLE_COUNT=${1:-10000}

echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║    FOLDER-BASED vs SINGLE-FILE ORGANIZATION COMPARISON             ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""
echo "Comparing data organization strategies with $TABLE_COUNT tables..."
echo ""

# Build the project
echo "🔨 Building project..."
./gradlew clean build -x test -q

# Run the comparison
echo ""
echo "🚀 Running folder comparison..."
echo ""
java -cp build/libs/json-avro-comparison-1.0.0.jar:$(./gradlew printClasspath -q) \
    com.learning.comparison.FolderComparisonApp "$TABLE_COUNT"

echo ""
echo "✅ Comparison complete!"
echo ""

