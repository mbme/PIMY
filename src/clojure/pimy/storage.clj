(ns pimy.storage
  (:use [pimy.utils.helpers :only [not-nil? throw-IAE]]
        metis.core)
  (:require [clojure.tools.logging :as log]
            [pimy.utils.backend :as backend]))

(defn initialize []
  (.createTables backend/db))

(defvalidator record-validator
  [:id :presence {:except :create}]
  [:title :presence ]
  [:text :presence ]
  [:tags :length {:greater-than 0}]
  [:type :inclusion {:in backend/record_types :only :create}])

(defn check-record
  [m mode]
  (let [errs (record-validator m mode)]
    (if-not (empty? errs)
      (throw-IAE errs))
    ))

(defn read-record
  "Return record with gived id or nil"
  [id]
  (if (number? id) (backend/get-record id)))

(defn create-record
  "Creates record and returns its id or throws
  error if required fields missing"
  [record]
  (log/debug "Creating record" record)
  (check-record record :create )
  (backend/create-record (select-keys record [:title :text :type :tags ])))

(defn update-record
  "Updates record fields and return record id or
  throws error if there is no record with specified id
  or some fields missing"
  [record]
  (log/debug "Updating record" record)
  (check-record record :update )
  (backend/update-record (select-keys record [:id :title :text :tags ]))
  (record :id ))

(defn delete-record
  "Returns true if record has been deleted false otherwise"
  [id]
  (log/debug "Deleting record" id)
  (backend/delete-record id)
  )

(defn read-tags
  "Returns all existing tags"
  []
  (backend/get-tags))

