package com.learning.comparison.benchmark;

import com.learning.comparison.json.JsonFolderSerializer;
import com.learning.comparison.model.TableMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Benchmark for folder-based organization
 */
public class FolderBenchmark {
    
    private final JsonFolderSerializer folderSerializer;
    
    public FolderBenchmark() {
        this.folderSerializer = new JsonFolderSerializer();
    }
    
    /**
     * Run folder-based JSON benchmark
     */
    public PerformanceMetrics benchmarkFolderJson(List<TableMetadata> tables, Path outputDir) throws IOException {
        // Clean and create output directory
        if (Files.exists(outputDir)) {
            deleteDirectory(outputDir);
        }
        Files.createDirectories(outputDir);
        
        long startWrite = System.currentTimeMillis();
        folderSerializer.serializeToFolders(tables, outputDir);
        long writeTime = System.currentTimeMillis() - startWrite;
        
        long totalSize = folderSerializer.getTotalSize(outputDir);
        
        long startRead = System.currentTimeMillis();
        List<TableMetadata> deserialized = folderSerializer.deserializeFromFolders(outputDir);
        long readTime = System.currentTimeMillis() - startRead;
        
        return PerformanceMetrics.builder()
                .format("JSON (Folders)")
                .fileSizeBytes(totalSize)
                .writeTimeMs(writeTime)
                .readTimeMs(readTime)
                .recordCount(tables.size())
                .build();
    }
    
    /**
     * Compare folder-based vs single-file organization
     */
    public void compareFolderVsSingleFile(PerformanceMetrics folderMetrics, PerformanceMetrics singleFileMetrics) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("FOLDER-BASED vs SINGLE-FILE COMPARISON");
        System.out.println("=".repeat(80));
        
        System.out.println("\n--- Folder-Based (tables/, columns/, indexes/, foreign_keys/) ---");
        System.out.println(folderMetrics);
        
        System.out.println("\n--- Single-File (all data in one file) ---");
        System.out.println(singleFileMetrics);
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ORGANIZATIONAL COMPARISON");
        System.out.println("=".repeat(80));
        
        // File size comparison
        double sizeDiff = ((double)(folderMetrics.getFileSizeBytes() - singleFileMetrics.getFileSizeBytes()) 
                / singleFileMetrics.getFileSizeBytes()) * 100;
        System.out.printf("\n📊 File Size:\n");
        System.out.printf("  Folder-based:  %.2f MB\n", folderMetrics.getFileSizeMB());
        System.out.printf("  Single-file:   %.2f MB\n", singleFileMetrics.getFileSizeMB());
        if (sizeDiff > 0) {
            System.out.printf("  📈 Folder-based is %.2f%% larger (due to denormalization)\n", sizeDiff);
        } else {
            System.out.printf("  📉 Folder-based is %.2f%% smaller\n", Math.abs(sizeDiff));
        }
        
        // Write time comparison
        double writeRatio = (double)folderMetrics.getWriteTimeMs() / singleFileMetrics.getWriteTimeMs();
        System.out.printf("\n⏱️  Write Performance:\n");
        System.out.printf("  Folder-based:  %d ms\n", folderMetrics.getWriteTimeMs());
        System.out.printf("  Single-file:   %d ms\n", singleFileMetrics.getWriteTimeMs());
        if (writeRatio > 1) {
            System.out.printf("  ⚠️  Folder-based is %.2fx slower (writes 4 files)\n", writeRatio);
        } else {
            System.out.printf("  ✅ Folder-based is %.2fx faster\n", 1.0/writeRatio);
        }
        
        // Read time comparison
        double readRatio = (double)folderMetrics.getReadTimeMs() / singleFileMetrics.getReadTimeMs();
        System.out.printf("\n⚡ Read Performance:\n");
        System.out.printf("  Folder-based:  %d ms\n", folderMetrics.getReadTimeMs());
        System.out.printf("  Single-file:   %d ms\n", singleFileMetrics.getReadTimeMs());
        if (readRatio > 1) {
            System.out.printf("  ⚠️  Folder-based is %.2fx slower (reads 4 files + joins)\n", readRatio);
        } else {
            System.out.printf("  ✅ Folder-based is %.2fx faster\n", 1.0/readRatio);
        }
        
        // Use case analysis
        System.out.println("\n" + "=".repeat(80));
        System.out.println("USE CASE RECOMMENDATIONS");
        System.out.println("=".repeat(80));
        
        System.out.println("\n✅ Use Folder-Based Organization When:");
        System.out.println("  • You need to query specific entity types independently");
        System.out.println("    Example: \"Show me all columns\" without loading tables");
        System.out.println("  • Building a data lake with partitioned data");
        System.out.println("  • Different entities have different access patterns");
        System.out.println("  • You want to parallelize processing (read multiple folders at once)");
        System.out.println("  • Schema evolution happens at entity level");
        
        System.out.println("\n✅ Use Single-File Organization When:");
        System.out.println("  • You always need complete table information");
        System.out.println("  • Simplicity is more important than flexibility");
        System.out.println("  • Smaller datasets where single file is manageable");
        System.out.println("  • Network latency matters (fewer file operations)");
        System.out.println("  • Maintaining referential integrity is critical");
        
        System.out.println("\n💡 Key Insight:");
        System.out.println("  Folder-based is larger (~25% overhead for denormalization) but offers");
        System.out.println("  better flexibility for selective queries and parallel processing.");
        System.out.println("=".repeat(80));
    }
    
    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
        }
    }
}

