package pimy.backend.db;

/**
 * Transaction isolation levels for H2 database.
 */
public enum IsolationLevel {
    READ_UNCOMMITTED(0),
    READ_COMMITTED(3),
    SERIALIZABLE(1);

    private final int level;

    IsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
