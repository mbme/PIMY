package pimy.backend.db;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Class to manage db.
 */
public class DBManager {
    private static final Logger LOG = Logger.getLogger(DBManager.class);

    final Dao<Record, Long> recordsDao;

    final Dao<Tag, Long> tagsDao;

    final Dao<RecordTag, Long> recordTagsDao;

    private final JdbcPooledConnectionSource connectionSource;

    public DBManager(Map<String, String> params) throws PropertyVetoException, SQLException {
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
        addShutdownHook();

        // todo auto initialize with reflection/search in package
        recordsDao = DaoManager.createDao(connectionSource, Record.class);
        tagsDao = DaoManager.createDao(connectionSource, Tag.class);
        recordTagsDao = DaoManager.createDao(connectionSource, RecordTag.class);
        LOG.info("Initialized DAO");
    }

    public void createTables() throws SQLException {
        LOG.info("Started DB tables creation");
        TableUtils.createTableIfNotExists(connectionSource, Record.class);
        TableUtils.createTableIfNotExists(connectionSource, Tag.class);
        TableUtils.createTableIfNotExists(connectionSource, RecordTag.class);
        LOG.info("Finished DB tables creation");
    }

    public Record createRecord(Record record) throws SQLException {
        DateTime dateTime = DateTime.now();

        record.setCreatedOn(dateTime);
        record.setUpdatedOn(dateTime);

        recordsDao.create(record);

        return record;
    }

    public Record readRecord(Long id) throws SQLException {
        return recordsDao.queryForId(id);
    }

    public Record updateRecord(Record rec) throws SQLException {
        Record prev = recordsDao.queryForSameId(rec);

        rec.setType(prev.getType());
        rec.setCreatedOn(prev.getCreatedOn());
        rec.setUpdatedOn(DateTime.now());

        recordsDao.update(rec);
        return rec;
    }

    public void deleteRecord(Long id) throws SQLException {
        recordsDao.deleteById(id);
    }

    private void addShutdownHook() {
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
