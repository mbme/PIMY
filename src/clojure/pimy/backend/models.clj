(ns pimy.backend.models
  (:import [org.apache.commons.beanutils BeanUtils]
           [pimy.backend.db Record DBManager RecordType]
           [java.util HashMap])
  (:use [pimy.utils :only [now config]]
        [clojure.set :only [rename-keys map-invert]]
        [clojure.walk :only [stringify-keys]]))

; todo rename this
; todo move to utils
(defn to-java-map [map]
  (HashMap. (stringify-keys map)))

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

(defn create-record
  [rec]
  (from-rec (.createRecord db (to-rec rec))))


(defn get-record
  [id]
  (from-rec (.readRecord db id)))


(defn update-record
  [rec]
  (from-rec (.updateRecord db (to-rec rec))))

(defn delete-record
  [id]
  (if (nil? (.deleteRecord db id))
    (throw (IllegalArgumentException. (str "Can't find record with id " id)))
    ))

(defn get-tags []
  (map from-tag (.getTags db)))
