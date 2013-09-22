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

(defn- created-now
  [record]
  (assoc record :created (now)))

(defn- get-record-id
  [result]
  (first (vals result)))

(defn- remove-nil
  [m]
  (into {} (remove (comp nil? val) m)))

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

(defn create-record [record]
  (log/debug "Creating record" record)
  (let [rec (created-now (get-fields record :title :text ))]
    (sql/with-connection (db-connection)
      (get-record-id (sql/insert-record :records rec)))
    ))

(defn read-record [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["SELECT * FROM records WHERE id = ?" id]
      (cond (empty? results) nil :else (first results)))))

(defn update-record [record]
  (log/debug "Updating record" record)
  (let [rec (get-fields record :id :title :text )
        id (rec :id )]
    (sql/with-connection (db-connection)
      (if (= 0 (first (sql/update-values :records ["id=?" id] rec)))
        (throw (IllegalArgumentException. (str "can't find record with id=" id)))
        id))
    ))

