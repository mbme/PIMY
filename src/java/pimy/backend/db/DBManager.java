package pimy.backend.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.DatabaseConnection;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Class to manage db.
 */
public class DBManager {
    private static final Logger LOG = LoggerFactory.getLogger(DBManager.class);

    final Dao<Record, Long> recordsDao;

    final Dao<Tag, Long> tagsDao;

    final Dao<RecordTag, Long> recordTagsDao;

    final JdbcPooledConnectionSource connectionSource;

    private final TagsManager tagsManager;

    public DBManager(Map<String, String> params) throws SQLException {
        connectionSource = new JdbcPooledConnectionSource();
        connectionSource.setUrl(params.get("db_url"));
        connectionSource.setUsername(params.get("db_user"));
        connectionSource.setPassword(params.get("db_password"));

        // only keep the connections open for 5 minutes
        connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        // change the check-every milliseconds from 30 seconds to 60
        connectionSource.setCheckConnectionsEveryMillis(60 * 1000);
        connectionSource.initialize();
        LOG.info("Established DB connection");

        registerShutdownHook();
        LOG.info("Registered shutdown hook");

        recordsDao = DaoManager.createDao(connectionSource, Record.class);
        tagsDao = DaoManager.createDao(connectionSource, Tag.class);
        recordTagsDao = DaoManager.createDao(connectionSource, RecordTag.class);

        tagsManager = new TagsManager(this);
        LOG.info("Initialized DAO");
    }

    /**
     * Creates required tables.
     */
    public void createTables() {
        try {
            LOG.info("Started DB tables creation");

            connectionSource.getReadWriteConnection().executeStatement(
                    "RUNSCRIPT FROM 'classpath:schema.sql'",
                    DatabaseConnection.DEFAULT_RESULT_FLAGS
            );

            LOG.info("Finished DB tables creation");

        } catch (SQLException e) {
            LOG.error("Exception while loading DB schema", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates new record.
     * Sets created_on and updated_on dates.
     *
     * @param record record to create
     * @return created record
     * @throws SQLException if something goes wrong
     */
    public Record createRecord(Record record) throws SQLException {
        DateTime dateTime = DateTime.now();
        record.setCreatedOn(dateTime);
        record.setUpdatedOn(dateTime);

        recordsDao.create(record);
        tagsManager.tagRecord(record, record.getTags());

        return record;
    }

    /**
     * Retrieves record with specified id.
     *
     * @param id id of required record
     * @return retrieved record or null if not found
     * @throws SQLException if something goes wrong
     */
    public Record readRecord(Long id) throws SQLException {
        Record record = recordsDao.queryForId(id);
        if (record != null) {
            tagsManager.loadRecordTags(record);
        }
        return record;
    }

    /**
     * Updates specified record in single transaction.
     *
     * @param rec record with updated fields
     * @return updated record
     * @throws SQLException if something goes wrong
     */
    public Record updateRecord(final Record rec) throws SQLException {
        Callable<Record> callable = new Callable<Record>() {
            @Override
            public Record call() throws Exception {
                try {
                    Record prev = recordsDao.queryForSameId(rec);

                    rec.setType(prev.getType());
                    rec.setCreatedOn(prev.getCreatedOn());
                    rec.setUpdatedOn(DateTime.now());
                    tagsManager.tagRecord(rec, rec.getTags());

                    recordsDao.update(rec);
                } catch (Throwable e) {
                    LOG.error("Error while updating record", e);
                }

                return rec;
            }
        };

        return transact(callable);
    }

    /**
     * Retrieves list of all tags.
     *
     * @return list of tags
     * @throws SQLException if something goes wrong
     */
    public List<Tag> getTags() throws SQLException {
        return tagsManager.getTags();
    }

    /**
     * Removes record with specified id.
     *
     * @param id id of record to remove
     * @return id of deleted record or null if record was not found
     * @throws SQLException if something goes wrong
     */
    public Long deleteRecord(Long id) throws SQLException {
        Record record = recordsDao.queryForId(id);

        if (record == null) {
            return null;
        }
        tagsManager.unTagRecord(record);
        recordsDao.delete(record);

        return id;
    }

    /**
     * Execute all operations in Callable in one transaction.
     */
    private <T> T transact(Callable<T> operations) throws SQLException {
        return TransactionManager.callInTransaction(connectionSource, operations);
    }

    /**
     * Register shutdown hook which will be executed when application is shutting down.
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (connectionSource != null && connectionSource.isOpen()) {
                    LOG.info("Closing DB connection...");
                    try {
                        connectionSource.close();
                        LOG.info("DB connection has been closed");
                    } catch (Throwable e) {
                        LOG.error("Exception while closing DB connection", e);
                    }
                }
            }
        }));
    }
}
