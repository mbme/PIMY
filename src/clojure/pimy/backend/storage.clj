(ns pimy.backend.storage
  (:import [pimy.backend.db DBManager])
  (:use [pimy.utils :only [now not-nil? throw-IAE]]
        metis.core)
  (:require [clojure.tools.logging :as log]
            [pimy.backend.models :as models]))

(defn initialize []
  (.createTables models/db))

(defn read-record
  "Return record with gived id or nil"
  [id]
  (if (number? id) (models/get-record id)))

(defn existing-rec-id
  ([map key _]
    (existing-rec-id (get map key)))
  ([id]
    (when-not (read-record id)
      "not existing record"
      )))

(defvalidator record-validator
  [:id :existing-rec-id {:except :create}]
  [:title :presence ]
  [:text :presence ]
  [:tags :length {:greater-than 0}]
  [:type :inclusion {:in models/record_types :only :create}])

(defn check-record
  [m mode]
  (let [errs (record-validator m mode)]
    (if-not (empty? errs)
      (throw-IAE errs))
    ))

(defn create-record
  "Creates record and returns its id or throws
  error if required fields missing"
  [record]
  (log/debug "Creating record" record)
  (check-record record :create )
  (models/create-record (select-keys record [:title :text :type :tags ])))

(defn update-record
  "Updates record fields and return record id or
  throws error if there is no record with specified id
  or some fields missing"
  [record]
  (log/debug "Updating record" record)
  (check-record record :update )
  (models/update-record (select-keys record [:id :title :text :tags ]))
  (record :id ))

(defn delete-record
  "Returns true if record has been deleted false otherwise"
  [id]
  (log/debug "Deleting record" id)
  (let [err (existing-rec-id id)]
    (if (nil? err)
      (models/delete-record id)
      (throw-IAE err id))
    ))

