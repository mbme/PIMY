(ns pimy.backend.models
  (:use korma.core
        pimy.backend.db))

(declare records tags)

(defentity records
  (table :RECORDS )
  (many-to-many tags :RECORDS_TAGS )
  (database db))

(defentity tags
  (table :TAGS )
  (database db))

(defn get-record
  [id]
  (first (select records (where {:id id}))))

