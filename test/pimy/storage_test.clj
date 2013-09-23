(ns pimy.storage-test
  (:use clojure.test
        [pimy.utils :only [not-nil?]])
  (:require [pimy.db]
            [pimy.storage :as storage]))

(deftest test-record
  (pimy.db/create-db)

  (testing "Successfull creation of record"
    (let [rec {:id nil :created nil :last_update nil :title "test" :text "some text"}
          rec1 {:id 2 :created nil :last_update nil :title "test" :text "some text"}
          rec2 {:title "test" :text "some text"}]

      (is (= (storage/create-record rec) 1))
      (is (= (storage/create-record rec1) 2))
      (is (= (storage/create-record rec2) 3))
      ))

  (testing "Failed creation of record"
    (let [
           bad_rec_1 {:id nil :created nil :last_update "test" :title "test" :text nil}
           bad_rec_2 {:id nil :created "test" :last_update nil :title nil :text "some text"}
           ]

      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_1)))
      (is (thrown? IllegalArgumentException (storage/create-record bad_rec_2)))
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

  (testing "Updating record"
    (testing "general update"
      (let [bad1 {:text "test"}
            rec {:title "test" :text "some text"}
            rec_id (storage/create-record rec)
            retrieved_rec1 (storage/read-record rec_id)
            ok {:id rec_id :title "new_title" :text "new_text"}
            upd_id (storage/update-record ok)
            retrieved_rec (storage/read-record upd_id)]

        (is (thrown? IllegalArgumentException (storage/update-record bad1))
          "must be an error if missing id")
        (is (= rec_id upd_id))
        (is (= (retrieved_rec :title ) "new_title"))
        (is (= (retrieved_rec :text ) "new_text"))
        (is (not= (retrieved_rec :created ) (retrieved_rec :last_update ))
          ":last_update must not be the same as :created")
        ))

    (testing "update of missing record"
      (let [rec {:title "test" :text "some text"}
            rec_id (storage/create-record rec)
            bad {:id (inc rec_id) :title "new_title" :text "new_text"}]

        (is (thrown? IllegalArgumentException (storage/update-record bad))
          "must be an error if updating non existing record")
        ))
    )

  (testing "Delete record"
    (let [rec {:title "some title" :text "some text"}
          rec_id (storage/create-record rec)]
      (is (storage/delete-record rec_id) "We should delete existing record")
      (is (not (storage/delete-record (+ rec_id 1))) "We can't delete missing record")
      ))
  )
