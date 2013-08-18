(ns pimy.handler
  (:use compojure.core
        ring.util.response
        [ring.middleware.format-params :only [wrap-restful-params]]
        [ring.middleware.format-response :only [wrap-restful-response]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes api-routes
  (context "/api" []
    (GET "/" []
      (->
       (response {:version "0.1.0"})
       (header "Allow" "OPTIONS"))))
  (route/not-found "Not found"))

(def app
  (-> (handler/api api-routes)
      (wrap-restful-params)
      (wrap-restful-response)))
