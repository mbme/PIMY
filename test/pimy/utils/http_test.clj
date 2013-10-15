(ns pimy.utils.http-test
  (:use clojure.test
        ring.mock.request
        pimy.utils.http))

(deftest test-wrap-exception-handler
  (testing "Exception handling"
    (let [response ((wrap-exception-handler
                      #(throw (Exception. "testing, 123")))
                     (request :get "/api"))]
      (is (= (response :status ) 500))
      (is (instance? Exception (response :body ))))))
