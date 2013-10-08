package pimy.backend.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Class to map table which represents many-to-many relationship.
 */
@DatabaseTable(tableName = "RECORDS_TAGS")
public class RecordTag {

    public static final String FIELD_ID = "ID";

    public static final String FIELD_RECORD_ID = "RECORD_ID";

    public static final String FIELD_TAG_ID = "TAG_ID";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private Long id;

    @DatabaseField(foreign = true, columnName = FIELD_RECORD_ID, canBeNull = false)
    private Record record;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_TAG_ID, canBeNull = false)
    private Tag tag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "RecordTag{" + "id=" + id + ", record=" + record + ", tag=" + tag + '}';
    }
}
