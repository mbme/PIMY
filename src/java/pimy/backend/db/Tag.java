package pimy.backend.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * Class to map tags table.
 */
public class Tag {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false, unique = true, width = 64)
    private String name;

    @DatabaseField(canBeNull = false)
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
