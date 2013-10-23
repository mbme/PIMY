(ns pimy.handler
  (:use compojure.core
        [pimy.utils.helpers :only [config]]
        ring.util.response
        [ring.middleware.format :only [wrap-restful-format]]
        [ring.middleware.format-params :only [wrap-json-params]]
        [ring.middleware.format-response :only [wrap-json-response]])
  (:require [compojure [handler :as handler] [route :as route]]
            [pimy.storage :as storage]
            [pimy.utils.http :as http]))

;todo IMPLEMENT CORRECT DATABASE INITIALIZATION
;todo 400 bad request instead of 500 for IllegalArgumentException

(defroutes api-routes
  (context "/api" []
    (GET "/" [] {:body {:version (config :version )}})
    (POST "/records" {params :body-params} {:body (storage/create-record params)})
    (ANY "/*" [] (not-found "not found")))

  (route/resources "/public")
  (GET "/*" [] (resource-response "/public/index.html")))

(def app
  (->
    (handler/api api-routes)

    (http/wrap-request-logger)
    (http/wrap-exception-handler)
    (http/wrap-response-logger)

    (wrap-restful-format)
    ))
