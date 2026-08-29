package kafkalab.stage2;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Connection + schema plumbing. Written for you — the lesson of stage 2 is the <em>queries</em>
 * and the <em>schema shape</em>, not JDBC boilerplate.
 *
 * <p>The database is a single file at {@code kafkalab/stage2-db-queue/data/queue.db}, gitignored.
 */
public final class Db {

    private static final Path FILE =
            Path.of(System.getProperty("queue.db", "kafkalab/stage2-db-queue/data/queue.db"));

    private Db() {}

    public static Connection open() throws SQLException {
        try {
            java.nio.file.Files.createDirectories(FILE.getParent());
        } catch (Exception e) {
            throw new SQLException("cannot create data dir", e);
        }
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + FILE);
        try (Statement s = c.createStatement()) {
            // Both of these matter once you run five processes against one file.
            //   WAL          - readers don't block the writer, writer doesn't block readers.
            //   busy_timeout - wait instead of instantly throwing "database is locked".
            // Without them stage 2 fails for reasons that have nothing to do with what it teaches.
            s.execute("PRAGMA journal_mode=WAL");
            s.execute("PRAGMA busy_timeout=5000");
        }
        return c;
    }

    /**
     * The deliberately naive starting schema: <b>one</b> status column for the whole row.
     *
     * <p>It is enough for one worker and wrong for four. Discovering exactly how it's wrong is
     * rung 2. When you change this DDL, delete {@code data/queue.db} so it gets recreated.
     */
    public static void init() throws SQLException {
        try (Connection c = open(); Statement s = c.createStatement()) {
            s.execute("""
                    CREATE TABLE IF NOT EXISTS outbox (
                      id         INTEGER PRIMARY KEY AUTOINCREMENT,
                      payload    TEXT    NOT NULL,
                      status     TEXT    NOT NULL DEFAULT 'NEW',
                      created_at INTEGER NOT NULL
                    )
                    """);
        }
    }

    /** Wipe everything. Used by demo.sh so each run starts clean. */
    public static void reset() throws SQLException {
        try (Connection c = open(); Statement s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS outbox");
        }
        init();
    }

    /** Row counts grouped by status — survives whatever columns you add later. */
    public static Map<String, Integer> statusCounts() throws SQLException {
        Map<String, Integer> out = new LinkedHashMap<>();
        try (Connection c = open();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT status, COUNT(*) FROM outbox GROUP BY status")) {
            while (rs.next()) {
                out.put(rs.getString(1), rs.getInt(2));
            }
        }
        return out;
    }

    public static int total() throws SQLException {
        try (Connection c = open();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM outbox")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
