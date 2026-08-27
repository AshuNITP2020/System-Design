package kafkalab.common;

/**
 * One downstream thing that must happen when an order is placed.
 *
 * <p>Every stage, from a monolith method call to a Kafka consumer group, ultimately calls
 * {@code apply}. Keeping this interface fixed across all nine stages is deliberate: it makes the
 * diff between stages purely about <em>transport</em>, never about business logic.
 */
public interface SideEffect {

    String name();

    /** May be slow. May throw. That is the point. */
    void apply(OrderPlaced order) throws Exception;
}
