(ns pimy.backend.models
  (:require [pimy.backend.db :as backend])
  (:use korma.core
        [korma.db :only [transaction]]
        [pimy.utils :only [now]]))

(declare records tags)

(defentity records
  (table :RECORDS )
  (many-to-many tags :RECORDS_TAGS )
  (database backend/db))

(defentity tags
  (table :TAGS )
  (many-to-many records :RECORDS_TAGS)
  (database backend/db))

(defentity records_tags
  (table :RECORDS_TAGS )
  (database backend/db))

(def record_types '("ARTICLE" "QUOTE"))
(defn rec-type? [type] (some #(= type %) record_types))

(defn- get-tag
  [name]
  (first
    (select tags (where {:name name}))
    ))

(defn- create-tag
  [name]
  (insert tags (values {:name name}))
  (get-tag name))

(defn- inc-tag [tag]
  (assoc tag :usages (+ (tag :usages ) 1)))

(defn- dec-tag [tag]
  (assoc tag :usages (- (tag :usages ) 1)))

(defn- update-tag
  [tag]
  (update tags
    (set-fields tag)
    (where {:id (tag :id )})))

(defn- get-or-create-tag
  [name]
  (let [tag (get-tag name)]
    (if-not tag
      (create-tag name)
      tag)
    ))

(defn- tag-record
  [tags rec_id]
  (transaction
    (doseq [tag tags] (update-tag (inc-tag tag)))
    (insert records_tags
      (values (map #(hash-map :record_id rec_id :tag_id (:id %)) tags)))
    ))

(defn get-record
  [id]
  (first
    (select records
      (where {:id id}))
    ))

(defn create-record
  [rec]
  (transaction
    (let [now (now)
          tags (map #(get-or-create-tag %) (rec :tags ))
          record (-> (dissoc rec :tags )
                   (assoc :created_on now)
                   (assoc :updated_on now))
          rec_id (-> (insert records (values record))
                   (vals)
                   (first))]
      (tag-record tags rec_id)
      (get-record rec_id)
      )))

(defn update-record
  [rec]
  (let [record (assoc rec :updated_on (now))]
    (update records
      (set-fields record)
      (where {:id (record :id )}))
    ))

(defn delete-record
  [id]
  (delete records
    (where {:id id})
    ))
