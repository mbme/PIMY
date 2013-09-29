(ns pimy.storage
  (:use pimy.db
        pimy.utils
        [clojure.string :only [join]])
  (:require [clojure.java.jdbc :as sql]
            [clj-time.core :as time]
            [clj-time.coerce :as time-conv]
            [clojure.tools.logging :as log]))

(defn- now
  []
  (time-conv/to-timestamp (time/now)))

(defn- get-record-id
  [result]
  (first (vals result)))

(defn- check-validity [m required-fields]
  (filter not-nil?
    (map #(if (nil? (m %1)) %1 nil) required-fields)))

(defn get-fields
  "Returns specified fields from map.
  If they are nil or missing - throw IllegalStateException"
  [m & fields]
  (let [missing-fields (check-validity m fields)]
    (if (empty? missing-fields)
      (select-keys m fields)
      (throw (IllegalArgumentException. (str "Missing fields: " (join ", " missing-fields)))))
    ))

(defn create-record
  "Creates record and returns its id or throws
  error if required fields missing"
  [record]
  (log/debug "Creating record" record)
  (let [now (now)
        rec (assoc (get-fields record :title :text :type) :created now :last_update now)]
    (sql/with-connection (db-connection)
      (get-record-id (sql/insert-record :records rec)))
    ))

(defn read-record
  "Return record with gived id or nil"
  [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["SELECT * FROM records WHERE id = ?" id]
      (cond (empty? results) nil :else (first results)))))

(defn update-record
  "Updates record fields and return record id or
  throws error if there is no record with specified id
  or some fields missing"
  [record]
  (log/debug "Updating record" record)
  (let [rec (assoc (get-fields record :id :title :text ) :last_update (now))
        id (rec :id )]
    (sql/with-connection (db-connection)
      (if (= 0 (first (sql/update-values :records ["id=?" id] rec)))
        (throw (IllegalArgumentException. (str "can't find record with id=" id)))
        id))
    ))

(defn delete-record
  "Returns true if record has beed deleted false othervise"
  [id]
  (log/debug "Deleting record" id)
  (sql/with-connection (db-connection)
    (= 1 (first (sql/delete-rows :records ["id=?" id])))
    ))

