(ns pimy.backend.storage-test
  (:use clojure.test
        [pimy.utils :only [not-nil?]])
  (:require [pimy.backend.db]
            [pimy.backend.storage :as storage]))

(defmacro is-IAE?
  [& body]
  `(is (~'thrown? IllegalArgumentException ~(first body)) ~(second body)))

(deftest test-record
  (pimy.backend.db/setup-db)

  (testing "Successfull creation of record"
    (let [rec {:id nil :created_on nil :updated_on nil :title "test" :text "some text" :type "ARTICLE"}
          rec1 {:id 2 :created_on nil :updated_on nil :title "test" :text "some text" :type "ARTICLE"}
          rec2 {:title "test" :text "some text" :type "ARTICLE"}]

      (is (= (storage/create-record rec) 1))
      (is (= (storage/create-record rec1) 2))
      (is (= (storage/create-record rec2) 3))
      ))

  (testing "Failed creation of record"
    (let [
           bad_rec_1 {:id nil :created_on nil :updated_on "test" :title "test" :text nil}
           bad_rec_2 {:id nil :created_on "test" :updated_on nil :title nil :text "some text"}
           bad_rec_3 {:id nil :created_on "test" :updated_on nil :title "test" :text "some text"}
           bad_rec_4 {:id nil :created_on "test" :updated_on nil :title "test" :text "some text" :type "WRONG"}
           ]

      (is-IAE? (storage/create-record bad_rec_1))
      (is-IAE? (storage/create-record bad_rec_2))
      (is-IAE? (storage/create-record bad_rec_3))
      (is-IAE? (storage/create-record bad_rec_4))
      ))


  (testing "Reading record"
    (let [rec {:title "test" :text "some text" :type "ARTICLE"}
          rec_id (storage/create-record rec)
          retrieved_rec (storage/read-record rec_id)]

      (is (= (retrieved_rec :id ) rec_id))
      (is (= (retrieved_rec :title ) (rec :title )))
      (is (= (retrieved_rec :text ) (rec :text )))
      (is (not-nil? (retrieved_rec :created_on )))
      (is (not-nil? (retrieved_rec :updated_on )))

      (is (nil? (storage/read-record (+ rec_id 1))))
      ))

  (testing "Failed reading of record with bad id"
    (let [rec {:title "test" :text "some text" :type "ARTICLE"}
          rec_id (storage/create-record rec)]
      (is (nil? (storage/read-record "test")))
      ))

  (testing "Updating record"
    (testing "general update"
      (let [bad1 {:text "test"}
            rec {:title "test" :text "some text" :type "ARTICLE"}
            rec_id (storage/create-record rec)
            retrieved_rec1 (storage/read-record rec_id)
            ok {:id rec_id :title "new_title" :text "new_text"}
            upd_id (storage/update-record ok)
            retrieved_rec (storage/read-record upd_id)]

        (is-IAE? (storage/update-record bad1) "must be an error if missing id")
        (is (= rec_id upd_id))
        (is (= (retrieved_rec :title ) "new_title"))
        (is (= (retrieved_rec :text ) "new_text"))
        (is (not= (retrieved_rec :created_on ) (retrieved_rec :updated_on ))
          ":updated_on must not be the same as :created_on")
        ))

    (testing "update of missing record"
      (let [rec {:title "test" :text "some text" :type "ARTICLE"}
            rec_id (storage/create-record rec)
            bad {:id (inc rec_id) :title "new_title" :text "new_text"}]

        (is-IAE? (storage/update-record bad) "must be an error if updating non existing record")
        ))
    )

  (testing "Delete record"
    (let [rec {:title "some title" :text "some text" :type "ARTICLE"}
          rec_id (storage/create-record rec)]
      (is (nil? (storage/delete-record rec_id)) "We should delete existing record")
      (is-IAE? (storage/delete-record (+ rec_id 1)) "We can't delete missing record")
      ))
  )
