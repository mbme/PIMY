(ns pimy.test.storage
  (:use clojure.test
        [pimy.utils :only [not-nil?]])
  (:require [pimy.db]
            [pimy.storage :as storage]))

(deftest test-record
  (pimy.db/create-db)

  (testing "Creation of record"
    (let [rec {:id nil :created nil :last_update nil :title "test" :text "some text"}
          bad_rec_1 {:id 2 :created nil :last_update nil :title "test" :text "some text"}
          bad_rec_2 {:id nil :created "test" :last_update nil :title "test" :text "some text"}
          bad_rec_3 {:id nil :created nil :last_update "test" :title "test" :text "some text"}
          bad_rec_4 {:id 2 :created "test" :last_update "test" :title "test" :text "some text"}]

      (is (= (storage/create-record rec) 1))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_1)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_2)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_3)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_4)))
      ))

  (testing "Reading record"
    (let [rec {:title "test" :text "some text"}
          rec_id (storage/create-record rec)
          retrieved_rec (storage/read-record rec_id)]

      (is (= (retrieved_rec :id ) rec_id))
      (is (= (retrieved_rec :title ) (rec :title )))
      (is (= (retrieved_rec :text ) (rec :text )))
      (is (not-nil? (retrieved_rec :created )))
      (is (not-nil? (retrieved_rec :last_update )))

      (is (nil? (storage/read-record (+ rec_id 1))))
      ))
  )
