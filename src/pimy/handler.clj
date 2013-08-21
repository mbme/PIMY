(ns pimy.handler
  (:use compojure.core
        pimy.middleware
        [pimy.http :as http]
        ring.util.response
        [ring.middleware.format-response :only [wrap-json-response]])
  (:require [compojure
             [handler :as handler]
             [route :as route]]))

(defroutes api-routes
  (context "/api" []
    (OPTIONS "/" []
      (http/options [:options ] {:version (:version config)}))
    (ANY "/" []
      (http/method-not-allowed [:options ]))
    (context "/records" []
      (GET "/" []
        (http/not-implemented))
      (GET "/:id" [id]
        (http/not-implemented))
      (HEAD "/:id" [id]
        (http/not-implemented))
      (POST "/" [:as req]
        (http/not-implemented))
      (PUT "/:id" [id]
        (http/not-implemented))
      (DELETE "/:id" [id]
        (http/not-implemented))
      (OPTIONS "/" []
        (http/options [:options :get :head :put :post :delete ]))
      (ANY "/" []
        (http/method-not-allowed [:options :get :head :put :post :delete ]))))
  (route/not-found "Not found"))

(def app
  (->
    (handler/api api-routes)
    (wrap-request-logger)
    (wrap-exception-handler)
    (wrap-response-logger)
    (wrap-json-response)))
