(ns pimy.handler
  (:use compojure.core
        ring.util.response
        pimy.middleware
        [pimy.http :as http]
        [ring.middleware.format-response :only [wrap-restful-response]]
        [clojure.java.io :only [resource]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.edn :as edn]))

(def config (edn/read-string (slurp (resource "properties.edn"))))
(println (:version config))

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
    (wrap-restful-response)))
