(ns pimy.handler-test
  (:use clojure.test
        ring.mock.request
        pimy.handler
        [cheshire.core :only [generate-string]]
        [pimy.storage-test :only [valid-rec is-IAE?]]
        ))

(defn request! [type url body]
  (-> (request type url)
    (content-type "application/json")
    (assoc :body-params body)
    ))

(defn request-post [url body]
  (request! :post url body))

(defn request-put [url body]
  (api-routes (request! :put url body)))

(defn request-delete [url id]
  (api-routes
    (request :delete (str url id))
    ))

(defn post-record []
  (((api-routes (request-post "/api/records" (valid-rec))) :body ) :id ))

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
      (is (= (count (response :body )) 1))
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

  (testing "API records GET one"
    (let [recId (post-record)
          response (api-routes (request :get (str "/api/records/" recId)))]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :id ))
      (is (contains? (response :body ) :title ))
      (is (contains? (response :body ) :text ))
      (is (contains? (response :body ) :tags ))
      (is (contains? (response :body ) :created_on ))
      (is (contains? (response :body ) :updated_on ))
      (is (contains? (response :body ) :type ))
      (is (= (count (response :body )) 7))
      ))

  (testing "API records GET one failed"
    (let [recId -1
          response (api-routes (request :get (str "/api/records/" recId)))]
      (is (= (response :status ) 404))
      ))

  (testing "API records PUT one"
    (let [recId (post-record)
          ok {:id recId :title "new_title" :text "new_text" :tags ["new one"]}
          response (request-put (str "/api/records/" recId) ok)]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :id ))
      (is (= (count (response :body )) 1))
      ))

  (testing "API records PUT one without id"
    (let [recId (post-record)
          ok {:title "new_title" :text "new_text" :tags ["new one"]}
          response (request-put (str "/api/records/" recId) ok)]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :id ))
      (is (= (count (response :body )) 1))
      ))

  (testing "API records PUT one failed"
    (let [recId (post-record)
          bad_id (inc recId)
          bad {:id bad_id :title "new_title" :text "new_text" :tags ["new one"]}]
      (is-IAE? (request-put (str "/api/records/" bad_id) bad))
      (is-IAE? (request-put (str "/api/records/" recId) bad))
      ))

  (testing "API records DELETE one"
    (let [recId (post-record)
          response (request-delete "/api/records/" recId)]
      (is (= (response :status ) 200))
      (is (contains? (response :body ) :id ))
      (is (= (count (response :body )) 1))
      ))

  (testing "API records DELETE one failed"
    (let [recId (post-record)
          bad_id (inc recId)]
      (is-IAE? (request-delete "/api/records/" bad_id))
      ))

  (testing "Not Found"
    (let [response (api-routes (request :get "/api/invalid"))]
      (is (= (response :status ) 404))
      ))
  )
