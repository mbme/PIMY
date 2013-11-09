(ns pimy.handler-test
  (:use clojure.test
        ring.mock.request
        pimy.handler
        [cheshire.core :only [generate-string]]
        [pimy.storage-test :only [valid-rec is-IAE?]]
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
      (is (contains? (response :body ) :version ))
      ))

  (testing "API records POST"
    (let [response (api-routes (request-post "/api/records" (valid-rec)))]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :id ))
      ))

  (testing "API records POST failed"
    (let [not-valid-rec (assoc (valid-rec) :tags "test")]
      (is-IAE? (api-routes (request-post "/api/records" not-valid-rec)))
      ))

  (testing "API records GET"
    (let [response (api-routes (request :get "/api/records"))]
      (is (= (response :status ) 200))
      (is (contains? (response :headers ) "X-Total-Count"))
      ))

  (testing "Not Found"
    (let [response (api-routes (request :get "/api/invalid"))]
      (is (= (response :status ) 404))
      ))
  )
