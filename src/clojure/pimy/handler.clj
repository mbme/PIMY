(ns pimy.handler
  (:use compojure.core
        [pimy.utils.http :as http]
        [pimy.utils.helpers :only [config]]
        ring.util.response
        [ring.middleware.format-response :only [wrap-json-response]])
  (:require [compojure
             [handler :as handler]
             [route :as route]]))

(defroutes api-routes
  (GET "/api" [] {:body {:version (config :version )}})
  (route/resources "/public")
  (GET "/*" [] (resource-response "/public/index.html")))

(def app
  (->
    (handler/api api-routes)
    (http/wrap-request-logger)
    (http/wrap-exception-handler)
    (http/wrap-response-logger)
    (wrap-json-response)))
