(ns pimy.backend.models-test
  (:import [org.joda.time DateTime])
  (:use clojure.test
        pimy.backend.models))

;todo move to utils
(defn remove-nil [map]
  (into {} (remove (comp nil? val) map)))

(deftest test-helpers
  (testing "Record serialization-deserialization"
    (let [now (DateTime.)
          rec {:id 1 :updated_on now :created_on now}]
      (is (= (-> (to-rec rec) (from-rec) (remove-nil)) rec))
      )))