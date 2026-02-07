package com.learning.comparison.benchmark;

import lombok.Builder;
import lombok.Data;

/**
 * Holds performance metrics for comparison
 */
@Data
@Builder
public class PerformanceMetrics {
    private String format;
    private long fileSizeBytes;
    private long writeTimeMs;
    private long readTimeMs;
    private int recordCount;
    
    public double getFileSizeMB() {
        return fileSizeBytes / (1024.0 * 1024.0);
    }
    
    public double getWriteThroughput() {
        return (recordCount / (writeTimeMs / 1000.0));
    }
    
    public double getReadThroughput() {
        return (recordCount / (readTimeMs / 1000.0));
    }
    
    @Override
    public String toString() {
        return String.format(
            "Format: %s\n" +
            "  File Size: %.2f MB (%d bytes)\n" +
            "  Write Time: %d ms (%.2f records/sec)\n" +
            "  Read Time: %d ms (%.2f records/sec)\n" +
            "  Record Count: %d",
            format,
            getFileSizeMB(),
            fileSizeBytes,
            writeTimeMs,
            getWriteThroughput(),
            readTimeMs,
            getReadThroughput(),
            recordCount
        );
    }
}

