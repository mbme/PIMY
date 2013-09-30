(ns pimy.backend.db
  (:import org.h2.tools.RunScript)
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:use [pimy.utils :only [config]]
        [korma.db]
        [korma.core]))

(def db-config (h2 {:db (:db_url config)
                    :user (:db_user config)
                    :password (:db_password config)
                    :naming {:keys str/lower-case
                             :fields str/upper-case}}))

;db instance
(defdb db db-config)
(defn db-conn [] (get-connection db))

(defn sql-connection
  "Returns sql connection for pooled db"
  []
  (.getConnection (:datasource (db-conn))))

(defn setup-db
  "Runs script which initializes db"
  []
  (log/info "Creating db")
  (with-open [connection (sql-connection)]
    (RunScript/execute
      connection
      (io/reader (io/resource "schema.sql"))))
  (log/info "Successfully finished creating db"))
