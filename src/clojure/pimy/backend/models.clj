(ns pimy.backend.models
  (:import [org.apache.commons.beanutils BeanUtils]
           [pimy.backend.db Record DBManager RecordType]
           [java.util HashMap])
  (:use [pimy.utils :only [now config]]
        [clojure.set :only [rename-keys map-invert]]
        [clojure.walk :only [stringify-keys]]))

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
  (-> rec
    (bean)
    (dissoc :class )
    (rename-keys db->map)
    ))

(defn create-record
  [rec]
  (from-rec (.createRecord db (to-rec rec))))

;
;(defn get-record
;  [id]
;  (first
;    (select records
;      (where {:id id}))
;    ))
;
;(defn create-record
;  [rec]
;  (transaction
;    (let [now (now)
;          tags (map #(get-or-create-tag %) (rec :tags ))
;          record (-> (dissoc rec :tags )
;                   (assoc :created_on now)
;                   (assoc :updated_on now))
;          rec_id (-> (insert records (values record))
;                   (vals)
;                   (first))]
;      (tag-record tags rec_id)
;      (get-record rec_id)
;      )))
;
;(defn update-record
;  [rec]
;  (let [record (assoc rec :updated_on (now))]
;    (update records
;      (set-fields record)
;      (where {:id (record :id )}))
;    ))
;
;(defn delete-record
;  [id]
;  (delete records
;    (where {:id id})
;    ))
