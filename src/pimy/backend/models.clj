(ns pimy.backend.models
  (:require [pimy.backend.db :as backend])
  (:use korma.core
        [pimy.utils :only [now]]))

(declare records tags)

(defentity records
  (table :RECORDS )
  (many-to-many tags :RECORDS_TAGS )
  (database backend/db))

(def record_types '("ARTICLE" "QUOTE"))
(defn rec-type? [type] (some #(= type %) record_types))

(defn get-record
  [id]
  (first
    (select records (where {:id id}))
    ))

(defn create-record
  [rec]
  (let [now (now)
        record (assoc rec :created_on now :updated_on now)]
    (-> (insert records (values record))
      (vals)
      (first))
    ))

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

(defentity tags
  (table :TAGS )
  (database backend/db))