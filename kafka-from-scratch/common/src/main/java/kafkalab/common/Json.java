package kafkalab.common;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Serialization is not what you're here to learn. One-liners, no ceremony. */
public final class Json {

    private static final ObjectMapper M = new ObjectMapper();

    private Json() {}

    public static String write(Object o) {
        try {
            return M.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("serialize failed", e);
        }
    }

    public static <T> T read(String json, Class<T> type) {
        try {
            return M.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("deserialize failed: " + json, e);
        }
    }

    public static byte[] bytes(Object o) {
        return write(o).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
