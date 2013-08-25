(ns pimy.test.storage
  (:use clojure.test)
  (:require [pimy.db]
            [pimy.storage :as storage]))

(deftest test-record
  (testing "Creation of record"
    (let [rec {:id nil :created nil :last_update nil :title "test" :text "some text"}]
      (pimy.db/create-db)

      (is (= (storage/create-record rec) 1)))))
