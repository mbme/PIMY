package pimy.backend.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class which incapsulates common tags managing options.
 */
public class TagsManager {

    private final DBManager dbManager;

    public TagsManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Tag> getOrCreate(Collection<String> names) throws SQLException {
        List<Tag> res = new ArrayList<Tag>(names.size());
        for (String name : names) {
            List<Tag> tags = dbManager.tagsDao.queryForEq(Tag.FIELD_NAME, name);
            int size = tags.size();
            if (size == 0) {
                Tag tag = new Tag();
                tag.setName(name);
                tag.setUsages(Tag.DEFAULT_USAGES);
                dbManager.tagsDao.create(tag);
                res.add(tag);
            } else if (size == 1) {
                Tag tag = tags.get(0);
                //ensure if we have no duplicates
                if (!res.contains(tag)) {
                    res.add(tag);
                }
            } else {
                throw new IllegalStateException(
                        "Tags query returned " + size + " results"
                );
            }
        }

        return res;
    }

    private void incUsages(Iterable<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            tag.setUsages(tag.getUsages() + 1);
        }
        save(tags);
    }

    private void decUsages(Iterable<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            tag.setUsages(tag.getUsages() - 1);
        }
        save(tags);
    }

    public void save(Iterable<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            dbManager.tagsDao.update(tag);
        }
    }

    public void tagRecord(Record record, Collection<String> tagsNames) throws SQLException {
        unTagRecord(record);

        List<Tag> tags = getOrCreate(tagsNames);
        incUsages(tags);

        for (Tag tag : tags) {
            RecordTag recordTag = new RecordTag();
            recordTag.setRecord(record);
            recordTag.setTag(tag);
            dbManager.recordTagsDao.create(recordTag);
        }

        loadRecordTags(record);
    }

    public void unTagRecord(Record record) throws SQLException {
        List<RecordTag> recordTags = getRecordTags(record);

        Collection<Tag> tags = new ArrayList<Tag>(recordTags.size());
        for (RecordTag recordTag : recordTags) {
            tags.add(recordTag.getTag());
        }

        dbManager.recordTagsDao.delete(recordTags);

        decUsages(tags);
        removeUnusedTags();
    }

    private void removeUnusedTags() throws SQLException {
        List<Tag> tags = dbManager.tagsDao.queryForEq(Tag.FIELD_USAGES, 0);
        dbManager.tagsDao.delete(tags);
    }

    private List<RecordTag> getRecordTags(Record record) throws SQLException {
        return dbManager.recordTagsDao.queryForEq(RecordTag.FIELD_RECORD_ID, record);
    }

    public void loadRecordTags(Record record) throws SQLException {
        List<RecordTag> recordTags = getRecordTags(record);
        List<String> tagNames = new ArrayList<String>(recordTags.size());
        for (RecordTag recordTag : recordTags) {
            tagNames.add(recordTag.getTag().getName());
        }
        record.setTags(tagNames);
    }

    public List<Tag> getTags() throws SQLException {
        return dbManager.tagsDao.queryForAll();
    }
}
