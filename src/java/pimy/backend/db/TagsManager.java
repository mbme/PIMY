package pimy.backend.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class which incapsulates common tags managing options.
 */
public class TagsManager {
    private static final Logger LOG = LoggerFactory.getLogger(TagsManager.class);

    private final DBManager dbManager;

    public TagsManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Converts list of tag names to list of tags.
     * Also it removes duplicates.
     *
     * @param names list of tags names
     * @return list of tags
     * @throws SQLException if something goes wrong
     */
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
                LOG.debug("Created new tag {}", tag);
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

    /**
     * Increments usages of each tag in list and saves changes.
     */
    private void incUsages(Iterable<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            tag.setUsages(tag.getUsages() + 1);
        }
        save(tags);
    }

    /**
     * Decrements usages of each tag in list and saves changes.
     */
    private void decUsages(Iterable<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            tag.setUsages(tag.getUsages() - 1);
        }
        save(tags);
    }

    /**
     * Updates each of tags.
     *
     * @param tags tags to update in database
     * @throws SQLException if something goes wrong
     */
    public void save(Iterable<Tag> tags) throws SQLException {
        for (Tag tag : tags) {
            dbManager.tagsDao.update(tag);
        }
    }

    /**
     * Replaces record tags with specified tags.
     *
     * @param record    record we want to tag
     * @param tagsNames list of tags which should be applied to record
     * @throws SQLException if something goes wrong
     */
    public void tagRecord(Record record, Collection<String> tagsNames) throws SQLException {
        unTagRecord(record);

        List<Tag> tags = getOrCreate(tagsNames);
        incUsages(tags);

        for (Tag tag : tags) {
            RecordTag recordTag = new RecordTag();
            recordTag.setRecord(record);
            recordTag.setTag(tag);
            dbManager.recordTagsDao.create(recordTag);
            LOG.debug("Tagged record {} with tag {}", record, tag);
        }

        loadRecordTags(record);
    }

    /**
     * Untags record and removes unused tags.
     *
     * @param record record which should be untagged
     * @throws SQLException if something goes wrong
     */
    public void unTagRecord(Record record) throws SQLException {
        List<RecordTag> recordTags = getRecordTags(record);

        Collection<Tag> tags = new ArrayList<Tag>(recordTags.size());
        for (RecordTag recordTag : recordTags) {
            tags.add(recordTag.getTag());
        }

        dbManager.recordTagsDao.delete(recordTags);
        LOG.debug("Removed {} tags from record {}", recordTags.size(), record);
        decUsages(tags);

        //tags should be removed if they are not in use
        removeUnusedTags();
    }

    /**
     * Removes tags which usage is 0.
     */
    private void removeUnusedTags() throws SQLException {
        List<Tag> tags = dbManager.tagsDao.queryForEq(Tag.FIELD_USAGES, 0);
        dbManager.tagsDao.delete(tags);
        LOG.debug("Removed {} unused tag(s)", tags.size());
    }

    private List<RecordTag> getRecordTags(Record record) throws SQLException {
        return dbManager.recordTagsDao.queryForEq(RecordTag.FIELD_RECORD_ID, record);
    }

    /**
     * Loads record tags and sets their names into record.
     *
     * @param record record to load tags for
     * @throws SQLException if something goes wrong
     */
    public void loadRecordTags(Record record) throws SQLException {
        List<RecordTag> recordTags = getRecordTags(record);
        List<String> tagNames = new ArrayList<String>(recordTags.size());
        for (RecordTag recordTag : recordTags) {
            tagNames.add(recordTag.getTag().getName());
        }
        LOG.debug("Loaded {} tags for record {}", tagNames.size(), record);
        record.setTags(tagNames);
    }

    /**
     * Retrieves list of all tags.
     *
     * @return list of tags
     * @throws SQLException if something goes wrong
     */
    public List<Tag> getTags() throws SQLException {
        List<Tag> tags = dbManager.tagsDao.queryForAll();
        LOG.debug("Loaded {} tags", tags.size());
        return tags;
    }
}
