(ns pimy.test.utils
  (:use clojure.test
        ring.mock.request
        pimy.utils))

(deftest test-wrap-exception-handler
  (testing "Exception handling"
    (let [response ((wrap-exception-handler
                      #(throw (Exception. "testing, 123")))
                     (request :get "/api"))]
      (is (= (response :status ) 500))
      (is (instance? Exception (response :body ))))))

(deftest test-validator
  (testing "map validator"
    (let [r {:id 2 :created nil :last_update nil :title "test" :text "some text"}
          v #{:title :text }
          v1 #{:title :text :id :created }
          v2 #{}
          v3 #{:title :text :id }
          errs (check-validity r v)
          errs1 (check-validity r v1)
          errs2 (check-validity r v2)
          errs3 (check-validity r v3)]
      (is (= (count errs) 1))
      (is (= (count errs1) 1))
      (is (= (count errs2) 3))
      (is (= (count errs3) 0)))))