#!/bin/bash

# Run JSON vs Avro Performance Comparison
# Usage: ./run_comparison.sh [table_count]

set -e

TABLE_COUNT=${1:-10000}

echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║         JSON vs AVRO PERFORMANCE COMPARISON                        ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""
echo "Running comparison with $TABLE_COUNT tables..."
echo ""

# Build the project
echo "🔨 Building project..."
./gradlew clean build -x test -q

# Run the comparison
echo ""
echo "🚀 Running performance comparison..."
echo ""
./gradlew run --args="$TABLE_COUNT" -q

# Display file sizes
echo ""
echo "📊 File Size Comparison:"
ls -lh output/ | grep -E "metadata\.(json|avro)"

echo ""
echo "✅ Comparison complete!"
echo ""
echo "💡 Tips:"
echo "  - View JSON: head -c 2000 output/metadata.json"
echo "  - View Avro schema: cat src/main/avro/table_metadata.avsc"
echo "  - Run with different counts: ./run_comparison.sh 50000"
echo ""

