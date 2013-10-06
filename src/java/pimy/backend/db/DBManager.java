package pimy.backend.db;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Map;

import org.joda.time.DateTime;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.H2DatabaseType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Class to manage db.
 */
public class DBManager {
    final Dao<Record, Long> recordsDao;

    final Dao<Tag, Long> tagsDao;

    final Dao<RecordTag, Long> recordTagsDao;

    private final JdbcPooledConnectionSource connectionSource;

    public DBManager(Map<String, String> params) throws PropertyVetoException, SQLException {
        connectionSource = new JdbcPooledConnectionSource();
        connectionSource.setUrl(params.get("db_url"));
        connectionSource.setUsername(params.get("db_user"));
        connectionSource.setPassword(params.get("db_password"));
        connectionSource.setDatabaseType(new H2DatabaseType());

        // only keep the connections open for 5 minutes
        connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        // change the check-every milliseconds from 30 seconds to 60
        connectionSource.setCheckConnectionsEveryMillis(60 * 1000);
        connectionSource.initialize();

        // todo auto initialize with reflection/search in package
        recordsDao = DaoManager.createDao(connectionSource, Record.class);
        tagsDao = DaoManager.createDao(connectionSource, Tag.class);
        recordTagsDao = DaoManager.createDao(connectionSource, RecordTag.class);
    }

    public void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Record.class);
        TableUtils.createTableIfNotExists(connectionSource, Tag.class);
        TableUtils.createTableIfNotExists(connectionSource, RecordTag.class);
    }

    public Record createRecord(Record record) throws SQLException {
        DateTime dateTime = DateTime.now();

        record.setCreatedOn(dateTime);
        record.setUpdatedOn(dateTime);

        recordsDao.create(record);

        return record;
    }
}
