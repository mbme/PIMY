package pimy.backend.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * Class to map tags table.
 */
public class Tag {

    public static final String FIELD_USAGES = "USAGES";

    public static final String FIELD_ID = "ID";

    public static final String FIELD_NAME = "NAME";

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
    public String toString() {
        return "Tag{" + "id=" + id + ", name='" + name + '\'' + ", usages=" + usages + '}';
    }
}
