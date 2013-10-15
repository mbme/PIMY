(ns pimy.utils.helpers_test
  (:use pimy.utils.helpers
        clojure.test)
  (:import [java.util HashMap]))

(deftest test-helpers
  (testing "converting to java map"
    (let [clj-map {:id "123" :TesT "val" "other" "key"}
          j-map (doto (HashMap.) (.put "id" "123") (.put "TesT" "val") (.put "other" "key"))]
      (is (= (to-java-map clj-map) j-map)))
    )

  (testing "removing nil values from map"
    (let [some-map {:id 123 :other nil}]
      (is (= (remove-nil some-map) {:id 123})))
    ))