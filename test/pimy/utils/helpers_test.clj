(ns pimy.utils.helpers_test
  (:use pimy.utils.helpers
        clojure.test
        [pimy.storage-test :only [is-IAE?]])
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
    )

  (testing "long parser"
    (is-IAE? (str->long "a"))
    (is-IAE? (str->long ""))
    (is-IAE? (str->long nil))
    (is (= 1 (str->long 1)))
    (is (= 1 (str->long "1")))
    (is (= 1 (str->long "01")))
    (is (= 1 (str->long "a" 1)))
    (is (= 1 (str->long nil 1)))
    )
  )