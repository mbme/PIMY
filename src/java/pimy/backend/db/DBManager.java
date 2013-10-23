package pimy.backend.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.DatabaseConnection;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pimy.backend.db.entities.Record;
import pimy.backend.db.entities.RecordTag;
import pimy.backend.db.entities.Tag;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Class to manage db.
 */
public class DBManager {

    public static final String DB_URL_PARAM = "db_url";

    public static final String DB_USER_PARAM = "db_user";

    public static final String DB_PASSWORD_PARAM = "db_password";

    private static final Logger LOG = LoggerFactory.getLogger(DBManager.class);

    final Dao<Record, Long> recordsDao;

    final Dao<Tag, Long> tagsDao;

    final Dao<RecordTag, Long> recordTagsDao;

    final JdbcPooledConnectionSource connectionSource;

    private final TagsManager tagsManager;

    private final Map<String, String> params;

    public DBManager(Map<String, String> params) throws SQLException {
        this.params = params;

        connectionSource = new JdbcPooledConnectionSource();
        initializeConnectionSource();
        initializeDbSchema();

        registerShutdownHook();

        recordsDao = DaoManager.createDao(connectionSource, Record.class);
        tagsDao = DaoManager.createDao(connectionSource, Tag.class);
        recordTagsDao = DaoManager.createDao(connectionSource, RecordTag.class);

        tagsManager = new TagsManager(this);
        LOG.info("Initialized DAO");
    }

    private void initializeConnectionSource() throws SQLException {
        connectionSource.setUrl(params.get(DB_URL_PARAM));
        connectionSource.setUsername(params.get(DB_USER_PARAM));
        connectionSource.setPassword(params.get(DB_PASSWORD_PARAM));

        // only keep the connections open for 5 minutes
        connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        // change the check-every milliseconds from 30 seconds to 60
        connectionSource.setCheckConnectionsEveryMillis(60 * 1000);

        connectionSource.initialize();

        LOG.info("Established DB connection");
    }

    /**
     * Creates required tables.
     */
    private void initializeDbSchema() {
        boolean skipTablesCreation = true;

        if (params.get(DB_URL_PARAM).contains(":mem:")) {
            skipTablesCreation = false;
        }

        if (skipTablesCreation) {
            LOG.info("Does not need to create tables");
            return;
        }

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
        LOG.info("Registered shutdown hook");
    }
}
