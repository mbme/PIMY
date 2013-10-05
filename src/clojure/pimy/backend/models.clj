(ns pimy.backend.models
  (:use [pimy.utils :only [now]]))

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
