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