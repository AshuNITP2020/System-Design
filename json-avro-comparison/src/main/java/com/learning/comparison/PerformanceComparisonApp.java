package com.learning.comparison;

import com.learning.comparison.benchmark.Benchmark;
import com.learning.comparison.benchmark.PerformanceMetrics;
import com.learning.comparison.ddl.DDLGenerator;
import com.learning.comparison.model.TableMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main application to demonstrate JSON vs Avro performance comparison
 * 
 * This simulates the real-world scenario mentioned in the resume:
 * "Migrated metadata storage from JSON to Avro achieving 60% reduction 
 * in file size through binary serialization and schema-based storage, 
 * with 3x faster read performance for loading 100,000+ table definitions"
 */
public class PerformanceComparisonApp {
    
    private static final String OUTPUT_DIR = "output";
    private static final String JSON_FILE = "metadata.json";
    private static final String AVRO_FILE = "metadata.avro";
    
    public static void main(String[] args) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("JSON vs AVRO PERFORMANCE COMPARISON");
            System.out.println("Simulating Database Metadata Storage for 100,000+ Tables");
            System.out.println("=".repeat(80));
            
            // Get table count from command line or use default
            int tableCount = args.length > 0 ? Integer.parseInt(args[0]) : 10000;
            
            System.out.println("\n🔧 Configuration:");
            System.out.println("  Table Count: " + tableCount);
            System.out.println("  Generating realistic table metadata...");
            
            // Generate table metadata (simulating DDL parsing)
            long startGen = System.currentTimeMillis();
            List<TableMetadata> tables = DDLGenerator.generateTableMetadata(tableCount);
            long genTime = System.currentTimeMillis() - startGen;
            
            System.out.println("  ✅ Generated " + tables.size() + " table definitions in " + genTime + " ms");
            
            // Display sample DDL
            if (!tables.isEmpty()) {
                System.out.println("\n📄 Sample DDL (for reference):");
                System.out.println("-".repeat(80));
                System.out.println(DDLGenerator.generateDDL(tables.get(0)));
                System.out.println("-".repeat(80));
            }
            
            // Create output directory
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            Path jsonPath = outputDir.resolve(JSON_FILE);
            Path avroPath = outputDir.resolve(AVRO_FILE);
            
            // Initialize benchmark
            Benchmark benchmark = new Benchmark();
            
            System.out.println("\n🚀 Running Benchmarks...\n");
            
            // Benchmark JSON
            System.out.println("1️⃣  Benchmarking JSON serialization...");
            PerformanceMetrics jsonMetrics = benchmark.benchmarkJson(tables, jsonPath);
            System.out.println("   ✅ JSON benchmark complete");
            
            // Benchmark Avro
            System.out.println("\n2️⃣  Benchmarking Avro serialization...");
            PerformanceMetrics avroMetrics = benchmark.benchmarkAvro(tables, avroPath);
            System.out.println("   ✅ Avro benchmark complete");
            
            // Compare results
            benchmark.compareResults(jsonMetrics, avroMetrics);
            
            // File locations
            System.out.println("\n📁 Output Files:");
            System.out.println("  JSON: " + jsonPath.toAbsolutePath());
            System.out.println("  Avro: " + avroPath.toAbsolutePath());
            
            System.out.println("\n✅ Comparison complete! Files saved to output directory.\n");
            
        } catch (IOException e) {
            System.err.println("❌ Error during benchmark: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid table count argument. Please provide a valid integer.");
        }
    }
}

