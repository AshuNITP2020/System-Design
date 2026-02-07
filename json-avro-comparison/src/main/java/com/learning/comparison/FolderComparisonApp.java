package com.learning.comparison;

import com.learning.comparison.benchmark.Benchmark;
import com.learning.comparison.benchmark.FolderBenchmark;
import com.learning.comparison.benchmark.PerformanceMetrics;
import com.learning.comparison.ddl.DDLGenerator;
import com.learning.comparison.model.TableMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Demonstrates folder-based organization vs single-file organization
 * 
 * This compares two data organization strategies:
 * 1. Folder-based: tables/, columns/, indexes/, foreign_keys/ (data lake pattern)
 * 2. Single-file: Everything in one JSON/Avro file
 */
public class FolderComparisonApp {
    
    private static final String OUTPUT_DIR = "output";
    private static final String SINGLE_FILE = "metadata_single.json";
    private static final String FOLDER_BASED = "metadata_folders";
    
    public static void main(String[] args) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FOLDER-BASED vs SINGLE-FILE ORGANIZATION COMPARISON");
            System.out.println("Comparing Data Lake Pattern vs Monolithic File");
            System.out.println("=".repeat(80));
            
            // Get table count from command line or use default
            int tableCount = args.length > 0 ? Integer.parseInt(args[0]) : 10000;
            
            System.out.println("\n🔧 Configuration:");
            System.out.println("  Table Count: " + tableCount);
            System.out.println("  Generating realistic table metadata...");
            
            // Generate table metadata
            long startGen = System.currentTimeMillis();
            List<TableMetadata> tables = DDLGenerator.generateTableMetadata(tableCount);
            long genTime = System.currentTimeMillis() - startGen;
            
            System.out.println("  ✅ Generated " + tables.size() + " table definitions in " + genTime + " ms");
            
            // Create output directory
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            Path singleFilePath = outputDir.resolve(SINGLE_FILE);
            Path folderBasedPath = outputDir.resolve(FOLDER_BASED);
            
            // Initialize benchmarks
            Benchmark singleFileBenchmark = new Benchmark();
            FolderBenchmark folderBenchmark = new FolderBenchmark();
            
            System.out.println("\n🚀 Running Benchmarks...\n");
            
            // Benchmark single-file JSON
            System.out.println("1️⃣  Benchmarking Single-File JSON...");
            PerformanceMetrics singleFileMetrics = singleFileBenchmark.benchmarkJson(tables, singleFilePath);
            System.out.println("   ✅ Single-file benchmark complete");
            
            // Benchmark folder-based JSON
            System.out.println("\n2️⃣  Benchmarking Folder-Based JSON...");
            PerformanceMetrics folderMetrics = folderBenchmark.benchmarkFolderJson(tables, folderBasedPath);
            System.out.println("   ✅ Folder-based benchmark complete");
            
            // Compare results
            folderBenchmark.compareFolderVsSingleFile(folderMetrics, singleFileMetrics);
            
            // Show folder structure
            System.out.println("\n📁 Output Structure:");
            System.out.println("\nSingle-File:");
            System.out.println("  " + singleFilePath.toAbsolutePath());
            System.out.printf("    Size: %.2f MB\n", singleFileMetrics.getFileSizeMB());
            
            System.out.println("\nFolder-Based:");
            System.out.println("  " + folderBasedPath.toAbsolutePath() + "/");
            showFolderStructure(folderBasedPath);
            
            System.out.println("\n✅ Comparison complete!\n");
            
            // Show sample queries
            System.out.println("💡 Example Use Cases:");
            System.out.println("\n  Folder-Based enables queries like:");
            System.out.println("    • \"Show all columns across all tables\" → Read columns/ only");
            System.out.println("    • \"List all indexes\" → Read indexes/ only");
            System.out.println("    • \"Find foreign key relationships\" → Read foreign_keys/ only");
            System.out.println("    • Parallel processing → Read all folders simultaneously");
            System.out.println("\n  Single-File requires:");
            System.out.println("    • Loading entire file for any query");
            System.out.println("    • Filtering in memory");
            System.out.println("    • Sequential processing");
            System.out.println();
            
        } catch (IOException e) {
            System.err.println("❌ Error during benchmark: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid table count argument. Please provide a valid integer.");
        }
    }
    
    private static void showFolderStructure(Path basePath) throws IOException {
        if (Files.exists(basePath)) {
            Path tablesPath = basePath.resolve("tables/metadata.json");
            Path columnsPath = basePath.resolve("columns/metadata.json");
            Path indexesPath = basePath.resolve("indexes/metadata.json");
            Path fkPath = basePath.resolve("foreign_keys/metadata.json");
            
            if (Files.exists(tablesPath)) {
                System.out.printf("    ├── tables/metadata.json (%.2f MB)\n", 
                        Files.size(tablesPath) / (1024.0 * 1024.0));
            }
            if (Files.exists(columnsPath)) {
                System.out.printf("    ├── columns/metadata.json (%.2f MB)\n", 
                        Files.size(columnsPath) / (1024.0 * 1024.0));
            }
            if (Files.exists(indexesPath)) {
                System.out.printf("    ├── indexes/metadata.json (%.2f MB)\n", 
                        Files.size(indexesPath) / (1024.0 * 1024.0));
            }
            if (Files.exists(fkPath)) {
                System.out.printf("    └── foreign_keys/metadata.json (%.2f MB)\n", 
                        Files.size(fkPath) / (1024.0 * 1024.0));
            }
        }
    }
}

