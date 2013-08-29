(ns pimy.storage
  (:use pimy.db
        [pimy.utils :only [check-validity]])
  (:require [clojure.java.jdbc :as sql]
            [clj-time.core :as time]
            [clj-time.coerce :as time-conv]
            [clojure.tools.logging :as log]))

(defn- now
  []
  (time-conv/to-timestamp (time/now)))

(defn- created-at
  [record date]
  (assoc record :created date))

(defn- updated-at
  [record date]
  (assoc record :last_update date))

(defn- get-record-id
  [result]
  (first (vals result)))

(defn- remove-nil
  [m]
  (into {} (remove (comp nil? val) m)))

(def validators {:for-create {:required-nil [:id :created :last_update ] :required [:title :text ]}
                 :for-update {:required [:id :last_update ]}})

(defn create-record [record]
  (log/debug "Creating record" record)
  (let [errs (check-validity record (validators :for-create ))
        now (now)
        rec (updated-at (created-at record now) now)]
    (if (empty? errs)
      (sql/with-connection (db-connection)
        (get-record-id (sql/insert-record
                         :records rec)))
      (throw (IllegalArgumentException. (apply str errs))))))

(defn read-record [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["SELECT * FROM records WHERE id = ?" id]
      (cond (empty? results) nil :else (first results)))))

(defn update-record [record]
  (log/debug "Updating record" record)
  (let [rec (updated-at (remove-nil (select-keys record [:id :title :text ])) (now))
        errs (check-validity rec (validators :for-update ))]
    (if (empty? errs)
      (sql/with-connection (db-connection)
        (if (= 0 (first (sql/update-values :records ["id=?" (rec :id )] rec)))
          (throw (IllegalArgumentException. (str "can't find record with id" (rec :id ))))
          (rec :id )))
      (throw (IllegalArgumentException. (apply str errs))))))

