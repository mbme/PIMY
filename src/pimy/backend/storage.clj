(ns pimy.backend.storage
  (:use pimy.backend.db
        [pimy.utils :only [now not-nil?]]
        metis.core
        [clojure.string :only [join]])
  (:require [clojure.java.jdbc :as sql]
            [clojure.tools.logging :as log]
            [pimy.backend.models :as models]))

(defn read-record
  "Return record with gived id or nil"
  [id]
  (if (number? id) (models/get-record id)))

(defn existing-rec-id
  [map key _]
  (when-not (read-record (get map key))
    "not existing record"
    ))

(defvalidator record-validator
  [:id :existing-rec-id {:except :create}]
  [:title :presence ]
  [:text :presence ]
  [:type :inclusion {:in models/record_types :only :create}])

(defn check-record
  [m mode]
  (let [errs (record-validator m mode)]
    (if-not (empty? errs)
      (throw (IllegalArgumentException. (str errs))))
    ))

(defn create-record
  "Creates record and returns its id or throws
  error if required fields missing"
  [record]
  (log/debug "Creating record" record)
  (check-record record :create )
  (models/create-record (select-keys record [:title :text :type ])))

(defn update-record
  "Updates record fields and return record id or
  throws error if there is no record with specified id
  or some fields missing"
  [record]
  (log/debug "Updating record" record)
  (check-record record :update )
  (models/update-record (select-keys record [:id :title :text ]))
  (record :id ))

(defn delete-record
  "Returns true if record has beed deleted false othervise"
  [id]
  (log/debug "Deleting record" id)
  (sql/with-connection (db-conn)
    (= 1 (first (sql/delete-rows :records ["id=?" id])))
    ))

