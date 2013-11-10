(ns pimy.utils.backend-test
  (:import [org.joda.time DateTime])
  (:use clojure.test
        pimy.utils.backend
        [pimy.utils.helpers :only [remove-nil]]))

(deftest test-backend
  (testing "Record serialization-deserialization"
    (let [now (DateTime.)
          rec {:id 1 :updated_on now :created_on now :tags ()}]
      (is (= (-> (to-rec rec) (from-rec) (remove-nil)) rec))
      )))