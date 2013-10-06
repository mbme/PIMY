package pimy.backend.db;

import org.joda.time.DateTime;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Class to map records table.
 */
public class Record {

    public static final String FIELD_ID = "ID";

    public static final String FIELD_TITLE = "TITLE";

    public static final String FIELD_TEXT = "TEXT";

    public static final String FIELD_TYPE = "TYPE";

    public static final String FIELD_CREATED_ON = "CREATED_ON";

    public static final String FIELD_UPDATED_ON = "UPDATED_ON";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private Long id;

    @DatabaseField(canBeNull = false, width = 512, columnName = FIELD_TITLE)
    private String title;

    @DatabaseField(canBeNull = false, columnName = FIELD_TEXT)
    private String text;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = FIELD_TYPE)
    private RecordType type;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = FIELD_CREATED_ON)
    private DateTime createdOn;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = FIELD_UPDATED_ON)
    private DateTime updatedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type == null ? null : this.type.toString();
    }

    public void setType(String type) {
        this.type = type == null ? null : RecordType.valueOf(type);
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(DateTime createdOn) {
        this.createdOn = createdOn;
    }

    public DateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(DateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "Record{" + "id=" + id + ", title='" + title + '\'' + ", text='" + text + '\'' + ", type=" + type
                + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + '}';
    }
}
