(ns pimy.test.storage
  (:use clojure.test)
  (:require [pimy.db]
            [pimy.storage :as storage]))

(deftest test-record
  (testing "Creation of record"
    (let [rec {:id nil :created nil :last_update nil :title "test" :text "some text"}
          bad_rec_1 {:id 2 :created nil :last_update nil :title "test" :text "some text"}
          bad_rec_2 {:id nil :created "test" :last_update nil :title "test" :text "some text"}
          bad_rec_3 {:id nil :created nil :last_update "test" :title "test" :text "some text"}
          bad_rec_4 {:id 2 :created "test" :last_update "test" :title "test" :text "some text"}]
      (pimy.db/create-db)

      (is (= (storage/create-record rec) 1))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_1)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_2)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_3)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_4))))))
