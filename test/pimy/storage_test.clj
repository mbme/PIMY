(ns pimy.storage-test
  (:use clojure.test
        [pimy.utils.helpers :only [not-nil?]])
  (:require [pimy.storage :as storage]))

(defmacro is-IAE?
  [& body]
  `(is (~'thrown? IllegalArgumentException ~(first body)) ~(second body)))

(defn valid-rec []
  (hash-map :title "test" :text "some text" :tags ["tag"]))

(defn get-tag [name]
  (first (filter #(= name (% :name )) (storage/list-tags))))

(deftest test-record
  (testing "Successfull creation of record"
    (are [id rec] (< 0 (:id (storage/create-record rec)))
      (assoc (valid-rec) :id 200 :created_on nil :updated_on "asdfd")
      (assoc (valid-rec) :tags ["tag" "asdf"])
      (assoc (valid-rec) :bad_prop "asdfas")
      (assoc (valid-rec) :type "WRONG")
      ))

  (testing "Failed creation of record"
    (are [rec] (thrown? IllegalArgumentException (storage/create-record rec))
      {:title "test" :text "" :tags ["tag"]}
      {:title "test" :text nil :tags ["tag"]}

      {:title "" :text "asdf" :tags ["tag"]}
      {:title nil :text "asdf" :tags ["tag"]}

      {:title "test" :text "asd" :tags []}
      (assoc (valid-rec) :tags "tag")
      {:title "test" :text "asd"}
      {}
      ))

  (testing "Reading record"
    (let [rec (valid-rec)
          rec_id ((storage/create-record rec) :id )
          rec1 (storage/read-record rec_id)]

      (is (= (rec1 :id ) rec_id))
      (is (= (rec1 :title ) (rec :title )))
      (is (= (rec1 :text ) (rec :text )))
      (is (= (rec1 :tags ) (rec :tags )))
      (is (not-nil? (rec1 :created_on )))
      (is (not-nil? (rec1 :updated_on )))
      (is (= (rec1 :created_on ) (rec1 :updated_on )))
      ))

  (testing "Failed reading of record with bad id"
    (let [rec_id ((storage/create-record (valid-rec)) :id )]
      (is (nil? (storage/read-record (+ rec_id 1))))
      (is (nil? (storage/read-record "test")))
      ))

  (testing "Updating record"
    (testing "general update"
      (let [rec (valid-rec)
            rec1 (storage/create-record rec)
            ok {:id (rec1 :id ) :title "new_title" :text "new_text" :tags ["new one"]}
            upd_id (storage/update-record ok)
            retrieved_rec (storage/read-record upd_id)]

        (is (= (rec1 :id ) upd_id))
        (is (= (retrieved_rec :title ) "new_title"))
        (is (= (retrieved_rec :text ) "new_text"))
        (is (= (ok :tags ) (retrieved_rec :tags )))
        (is (not= (retrieved_rec :created_on ) (retrieved_rec :updated_on ))
          ":updated_on must not be the same as :created_on")
        ))

    (testing "update of missing record"
      (let [rec (valid-rec)
            rec_id ((storage/create-record rec) :id )
            bad {:id (inc rec_id) :title "new_title" :text "new_text"}]

        (is-IAE? (storage/update-record bad) "must be an error if updating non existing record")
        ))
    )

  (testing "Delete record"
    (let [uniq-tag "some-very-uniq-tag"
          rec (assoc (valid-rec) :tags [uniq-tag])
          rec_id ((storage/create-record rec) :id )]
      (is (not-nil? (get-tag uniq-tag)))
      (is (nil? (storage/delete-record rec_id)) "We should delete existing record")
      (is (nil? (get-tag uniq-tag)))
      (is-IAE? (storage/delete-record (+ rec_id 1)) "We can't delete missing record")
      ))

  (testing "Retrieving all tags"
    (is (> (count (storage/list-tags)) 0))
    )

  (testing "Tags processing when zero usages"
    (let [custom-tag "my-very-custom-tag"
          rec (assoc (valid-rec) :tags [custom-tag])
          rec_id ((storage/create-record rec) :id )
          rec_id1 ((storage/create-record rec) :id )]
      (is (= ((get-tag custom-tag) :usages 2)))
      (storage/delete-record rec_id)
      (is (= ((get-tag custom-tag) :usages 1)))
      (storage/delete-record rec_id1)
      (is (nil? (get-tag custom-tag)))
      ))

  (testing "Similar lowercase/uppercase tags processing when creating record and tags trimming"
    (let [custom-tag "my-TAG"
          custom-tag-lower "my-tag"
          rec (assoc (valid-rec) :tags [custom-tag custom-tag custom-tag-lower (str custom-tag "     ")])
          rec_id ((storage/create-record rec) :id )
          rec1 (storage/read-record rec_id)]
      (is (= 1 (count (rec1 :tags ))))
      ))

  (testing "Retrieving all records"
    (let [total (count (storage/list-records))]
      (is (> total 0))
      (is (= (count (storage/list-records {:offset 0 :limit 1})) 1))
      (is (= (count (storage/list-records {:limit 1})) 1))
      (is (= (count (storage/list-records {:offset 1 :limit 2})) 2))
      (is (= (count (storage/list-records {:offset (- total 1) :limit 2})) 1))
      ))
  )
