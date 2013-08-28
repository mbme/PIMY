(ns pimy.storage
  (:use pimy.db
        [pimy.utils :only [check-validity]])
  (:require [clojure.java.jdbc :as sql]
            [clj-time.core :as time]
            [clj-time.coerce :as time-conv]
            [clojure.tools.logging :as log]))

(defn- created-now [record]
  (assoc record :created (time-conv/to-timestamp (time/now))))
(defn- updated-now [record]
  (assoc record :last_update (time-conv/to-timestamp (time/now))))
(defn- get-record-id [result]
  (first (vals result)))

(def new-rec-validator #{:title :text })

(defn read-record [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["SELECT * FROM records WHERE id = ?" id]
      (cond
        (empty? results) nil :else (first results)))))

(defn create-record [record]
  (log/debug "Creating record" record)
  (let [errs (check-validity record new-rec-validator)]
    (if (empty? errs)
      (sql/with-connection (db-connection)
        (get-record-id (sql/insert-record
                         :records (updated-now (created-now record)))))
      (throw (IllegalArgumentException. (str errs))))))

