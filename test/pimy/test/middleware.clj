(ns pimy.test.middleware
  (:use clojure.test
        ring.mock.request
        pimy.middleware))

(deftest test-wrap-exception-handler
  (testing "Exception handling"
    (let [response ((wrap-exception-handler
                      #(throw (Exception. "testing, 123")))
                     (request :get "/api"))]
      (is (= (response :status ) 500))
      (is (instance? Exception (response :body ))))))