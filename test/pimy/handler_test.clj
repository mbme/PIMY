(ns pimy.handler-test
  (:use clojure.test
        ring.mock.request
        pimy.handler
        [cheshire.core :only [generate-string]]
        [pimy.storage-test :only [valid-rec]]
        ))


(defn request-post [url body]
  (-> (request :post url)
    (content-type "application/json")
    (assoc :body-params body)
    ))

(deftest test-api-routes
  (testing "API GET"
    (let [response (api-routes (request :get "/api"))]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :version ))))

  (testing "API records POST"
    (let [response (api-routes (request-post "/api/records" (valid-rec)))]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :id ))))

  (testing "API records POST failed"
    (let [response (api-routes (request-post "/api/records" (assoc (valid-rec) :tags "test")))]
      (is (= (response :status ) 500))
      (is (contains? (response :body ) :exception ))
      (is (contains? (response :body ) :message ))
      ))

  (testing "Not Found"
    (let [response (api-routes (request :get "/api/invalid"))]
      (is (= (response :status ) 404)))))
