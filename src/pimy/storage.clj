(ns pimy.storage
  (:use pimy.db)
  (:require [clojure.java.jdbc :as sql]
            [clj-time.core :as time]
            [clj-time.coerce :as time-conv]
            [clojure.tools.logging :as log]))

(defn- created-now [record]
  (assoc record :created (time-conv/to-timestamp (time/now))))
(defn- updated-now [record]
  (assoc record :last_update (time-conv/to-timestamp (time/now))))

(defn get-record [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["SELECT * FROM records WHERE id = ?" id]
      (cond
        (empty? results) nil :else (first results)))))

(defn create-record [record]
  (log/debug "Creating record" record)
  (sql/with-connection (db-connection)
    (let [result (sql/insert-record :records (updated-now (created-now record)))]
      (first (vals result)))))

