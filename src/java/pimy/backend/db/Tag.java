package pimy.backend.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Class to map tags table.
 */
@DatabaseTable(tableName = "TAGS")
public class Tag {

    public static final String FIELD_USAGES = "USAGES";

    public static final String FIELD_ID = "ID";

    public static final String FIELD_NAME = "NAME";

    public static final int DEFAULT_USAGES = 0;

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private Long id;

    @DatabaseField(canBeNull = false, unique = true, width = 64, columnName = FIELD_NAME)
    private String name;

    @DatabaseField(canBeNull = false, columnName = FIELD_USAGES)
    private Integer usages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUsages() {
        return usages;
    }

    public void setUsages(Integer usages) {
        this.usages = usages;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }

        Tag tag = (Tag) obj;

        if (id != null ? !id.equals(tag.id) : tag.id != null) {
            return false;
        }

        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", usages=" + usages +
                '}';
    }
}
