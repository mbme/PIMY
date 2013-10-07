(ns pimy.backend.storage-test
  (:use clojure.test
        [pimy.utils :only [not-nil?]])
  (:require [pimy.backend.storage :as storage]))

(defmacro is-IAE?
  [& body]
  `(is (~'thrown? IllegalArgumentException ~(first body)) ~(second body)))

(defn valid-rec []
  (hash-map :title "test" :text "some text" :type "ARTICLE" :tags ["tag"]))

(deftest test-record
  (storage/initialize)

  (testing "Successfull creation of record"
    (are [id rec] (= id (:id (storage/create-record rec)))
      1 (assoc (valid-rec) :id 200 :created_on nil :updated_on "asdfd")
      2 (assoc (valid-rec) :tags ["tag" "asdf"])
      3 (assoc (valid-rec) :bad_prop "asdfas")
      ))

  (testing "Failed creation of record"
    (are [rec] (thrown? IllegalArgumentException (storage/create-record rec))
      {:title "test" :text "" :type "ARTICLE" :tags ["tag"]}
      {:title "test" :text nil :type "ARTICLE" :tags ["tag"]}

      {:title "" :text "asdf" :type "ARTICLE" :tags ["tag"]}
      {:title nil :text "asdf" :type "ARTICLE" :tags ["tag"]}

      {:title "test" :text "asd" :type "ARTICLE" :tags []}
      {:title "test" :text "asd" :type "ARTICLE"}

      {:title "test" :text "ok" :type "WRONG" :tags ["tag"]}
      {:title "test" :text "ok" :tags ["tag"]}
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
    (let [rec (valid-rec)
          rec_id ((storage/create-record rec) :id )]
      (is (nil? (storage/delete-record rec_id)) "We should delete existing record")
      (is-IAE? (storage/delete-record (+ rec_id 1)) "We can't delete missing record")
      ))
  )
