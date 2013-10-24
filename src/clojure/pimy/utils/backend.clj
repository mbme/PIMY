(ns pimy.utils.backend
  (:import [org.apache.commons.beanutils BeanUtils]
           [pimy.backend.db DBManager]
           [pimy.backend.db.entities RecordType Record])
  (:use [pimy.utils.helpers :only [config to-java-map not-nil? throw-IAE]]
        [clojure.set :only [rename-keys map-invert]]
        [clojure.walk :only [stringify-keys]]
        metis.core)
  (:require [clojure.tools.logging :as log]))

(def db (DBManager. (to-java-map config)))

(def record_types (map str (RecordType/values)))

(def db->map {:createdOn :created_on, :updatedOn :updated_on})
(def map->db (map-invert db->map))

(defn to-bean [map clazz]
  (let [instance (eval `(new ~clazz))]
    (BeanUtils/populate
      instance
      (stringify-keys (rename-keys map map->db)))
    instance
    ))

(defn to-rec [map]
  (to-bean map Record))

(defn from-rec [rec]
  (if-not (nil? rec)
    (-> rec
      (bean)
      (dissoc :class )
      (rename-keys db->map))
    ))

(defn from-tag [tag]
  (if-not (nil? tag)
    (-> tag
      (bean)
      (dissoc :class ))
    ))

(defn not-empty-vector [map key _]
  (let [val (get map key)]
    (if-not (and
              (vector? val)
              (< 0 (count val)))
      "must be not empty array")
    ))

(defvalidator record-validator
  [:id :presence {:except :create}]
  [:title :presence ]
  [:text :presence ]
  [:tags :not-empty-vector :presence ]
  [:type :inclusion {:in record_types :only :create}])

(defn check-record
  [m mode]
  (let [errs (record-validator m mode)]
    (if-not (empty? errs)
      (throw-IAE errs))
    ))

(defn create-record
  [rec]
  (log/debug "Creating record" rec)
  (check-record rec :create )
  (from-rec (.createRecord db (to-rec rec))))

(defn get-record
  [id]
  (from-rec (.readRecord db id)))

(defn list-records
  [offset limit]
  (log/debug "Querying {} records starting from {}" limit offset)
  (map from-rec (.listRecords db offset limit)))

(defn update-record
  [rec]
  (log/debug "Updating record" rec)
  (check-record rec :update )
  (from-rec (.updateRecord db (to-rec rec))))

(defn delete-record
  [id]
  (log/debug "Deleting record" id)
  (if (nil? (.deleteRecord db id))
    (throw (IllegalArgumentException. (str "Can't find record with id " id)))
    ))

(defn list-tags []
  (map from-tag (.listTags db)))
