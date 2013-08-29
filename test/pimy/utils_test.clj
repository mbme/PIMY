(ns pimy.utils-test
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
          v {:required-nil [:id ] :required [:title :text ]}
          v1 {:required [:title :text :created ]}
          v2 {}
          v3 {:required [:id :title :text ]}
          errs (check-validity r v)
          errs1 (check-validity r v1)
          errs2 (check-validity r v2)
          errs3 (check-validity r v3)]
      (is (= (count errs) 1) "must be error about not nil :id")
      (is (= (count errs1) 1) "must be error about nil :created")
      (is (= (count errs2) 0) "must be no errors when there are no validators")
      (is (= (count errs3) 0) "must be no errors when record is valid"))))