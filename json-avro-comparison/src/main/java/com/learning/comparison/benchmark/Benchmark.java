package com.learning.comparison.benchmark;

import com.learning.comparison.avro.AvroSerializer;
import com.learning.comparison.json.JsonSerializer;
import com.learning.comparison.model.TableMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Benchmark JSON vs Avro performance
 */
public class Benchmark {
    
    private final JsonSerializer jsonSerializer;
    private final AvroSerializer avroSerializer;
    
    public Benchmark() throws IOException {
        this.jsonSerializer = new JsonSerializer();
        this.avroSerializer = new AvroSerializer();
    }
    
    /**
     * Run JSON benchmark
     */
    public PerformanceMetrics benchmarkJson(List<TableMetadata> tables, Path outputPath) throws IOException {
        long startWrite = System.currentTimeMillis();
        jsonSerializer.serializeToFile(tables, outputPath);
        long writeTime = System.currentTimeMillis() - startWrite;
        
        long fileSize = Files.size(outputPath);
        
        long startRead = System.currentTimeMillis();
        List<TableMetadata> deserialized = jsonSerializer.deserializeFromFile(outputPath);
        long readTime = System.currentTimeMillis() - startRead;
        
        return PerformanceMetrics.builder()
                .format("JSON")
                .fileSizeBytes(fileSize)
                .writeTimeMs(writeTime)
                .readTimeMs(readTime)
                .recordCount(tables.size())
                .build();
    }
    
    /**
     * Run Avro benchmark
     */
    public PerformanceMetrics benchmarkAvro(List<TableMetadata> tables, Path outputPath) throws IOException {
        long startWrite = System.currentTimeMillis();
        avroSerializer.serializeToFile(tables, outputPath);
        long writeTime = System.currentTimeMillis() - startWrite;
        
        long fileSize = Files.size(outputPath);
        
        long startRead = System.currentTimeMillis();
        List<TableMetadata> deserialized = avroSerializer.deserializeFromFile(outputPath);
        long readTime = System.currentTimeMillis() - startRead;
        
        return PerformanceMetrics.builder()
                .format("AVRO")
                .fileSizeBytes(fileSize)
                .writeTimeMs(writeTime)
                .readTimeMs(readTime)
                .recordCount(tables.size())
                .build();
    }
    
    /**
     * Compare and print results
     */
    public void compareResults(PerformanceMetrics jsonMetrics, PerformanceMetrics avroMetrics) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERFORMANCE COMPARISON RESULTS");
        System.out.println("=".repeat(80));
        
        System.out.println("\n--- JSON Metrics ---");
        System.out.println(jsonMetrics);
        
        System.out.println("\n--- Avro Metrics ---");
        System.out.println(avroMetrics);
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("COMPARISON ANALYSIS");
        System.out.println("=".repeat(80));
        
        // File size comparison
        double sizeReduction = ((double)(jsonMetrics.getFileSizeBytes() - avroMetrics.getFileSizeBytes()) 
                / jsonMetrics.getFileSizeBytes()) * 100;
        System.out.printf("\n📊 File Size:\n");
        System.out.printf("  JSON:  %.2f MB\n", jsonMetrics.getFileSizeMB());
        System.out.printf("  Avro:  %.2f MB\n", avroMetrics.getFileSizeMB());
        System.out.printf("  ✅ Avro is %.2f%% smaller\n", sizeReduction);
        
        // Write time comparison
        double writeSpeedup = (double)jsonMetrics.getWriteTimeMs() / avroMetrics.getWriteTimeMs();
        System.out.printf("\n⏱️  Write Performance:\n");
        System.out.printf("  JSON:  %d ms (%.2f records/sec)\n", 
                jsonMetrics.getWriteTimeMs(), jsonMetrics.getWriteThroughput());
        System.out.printf("  Avro:  %d ms (%.2f records/sec)\n", 
                avroMetrics.getWriteTimeMs(), avroMetrics.getWriteThroughput());
        System.out.printf("  ✅ Avro is %.2fx faster\n", writeSpeedup);
        
        // Read time comparison
        double readSpeedup = (double)jsonMetrics.getReadTimeMs() / avroMetrics.getReadTimeMs();
        System.out.printf("\n⚡ Read Performance:\n");
        System.out.printf("  JSON:  %d ms (%.2f records/sec)\n", 
                jsonMetrics.getReadTimeMs(), jsonMetrics.getReadThroughput());
        System.out.printf("  Avro:  %d ms (%.2f records/sec)\n", 
                avroMetrics.getReadTimeMs(), avroMetrics.getReadThroughput());
        System.out.printf("  ✅ Avro is %.2fx faster\n", readSpeedup);
        
        // Summary
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SUMMARY");
        System.out.println("=".repeat(80));
        System.out.printf("✅ Size Reduction: %.1f%%\n", sizeReduction);
        System.out.printf("✅ Write Speed Improvement: %.1fx\n", writeSpeedup);
        System.out.printf("✅ Read Speed Improvement: %.1fx\n", readSpeedup);
        System.out.println("\n💡 Key Takeaways:");
        System.out.println("  • Avro's binary format significantly reduces file size");
        System.out.println("  • Schema-based serialization eliminates field name overhead");
        System.out.println("  • Direct binary-to-object mapping improves read/write speed");
        System.out.println("  • Ideal for large-scale metadata storage (100,000+ tables)");
        System.out.println("=".repeat(80));
    }
}

