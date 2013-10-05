package pimy.backend.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Class to map table which represents many-to-many relationship.
 */
@DatabaseTable(tableName = "records_tags")
public class RecordTag {
    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(foreign = true, columnName = "record_id", canBeNull = false)
    private Record record;

    @DatabaseField(foreign = true, columnName = "tag_id", canBeNull = false)
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
}
