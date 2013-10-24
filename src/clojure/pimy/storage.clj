(ns pimy.storage
  (:require [pimy.utils.backend :as backend])
  (:use [clojure.walk :only [keywordize-keys]]))

(defn read-record
  "Return record with gived id or nil"
  [id]
  (if (number? id) (backend/get-record id)))

(defn list-records
  "List all records. Pagination parameters would be retrieved from passed map"
  [& [{offset :offset limit :limit :or {offset 0 limit 10}}]]
  (backend/list-records offset limit))

(defn create-record
  "Creates record and returns its id or throws
  error if required fields are missing"
  [record]
  (-> record
    (keywordize-keys)
    (select-keys [:title :text :type :tags ])
    (assoc :type "ARTICLE")
    (backend/create-record)
    (select-keys [:id ])
    ))

(defn update-record
  "Updates record fields and return record id or
  throws error if there is no record with specified id
  or some fields missing"
  [record]
  (backend/update-record (select-keys record [:id :title :text :tags ]))
  (record :id ))

(defn delete-record
  "Returns true if record has been deleted false otherwise"
  [id]
  (backend/delete-record id)
  )

(defn list-tags
  "Returns all existing tags"
  []
  (backend/list-tags))

