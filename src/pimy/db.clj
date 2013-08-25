(ns pimy.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource
           org.h2.tools.RunScript)
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:use [pimy.utils :only [config]]))

(def db-config
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname (:db_url config)
   :user (:db_user config)
   :password (:db_password config)})

(defn pool
  "c3po pool on database"
  [config]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname config))
               (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
               (.setUser (:user config))
               (.setPassword (:password config))
               (.setMaxPoolSize 6)
               (.setMinPoolSize 1)
               (.setInitialPoolSize 1))]
    {:datasource cpds}))

(def pooled-db (delay (pool db-config)))

(defn db-connection [] @pooled-db)

(defn sql-connection [db-connection]
  "Get sql connection from connections pool"
  (.getConnection (:datasource (force db-connection))))

(defn create-db []
  (log/info "Creating db")
  (with-open [connection (sql-connection (db-connection))]
    (RunScript/execute
      connection
      (io/reader (io/resource "schema.sql"))))
  (log/info "Successfully finished creating db"))
