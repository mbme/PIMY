package pimy.backend.db;

import org.joda.time.DateTime;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.List;

/**
 * Class to map records table.
 */
public class Record {
    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(canBeNull = false, width = 512)
    private String title;

    @DatabaseField(canBeNull = false)
    private String text;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING)
    private RecordType type;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME)
    private DateTime createdOn;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME)
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

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
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
}
