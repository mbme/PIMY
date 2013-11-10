(ns pimy.handler
  (:use compojure.core
        [pimy.utils.helpers :only [config]]
        ring.util.response
        [ring.middleware.format-params :only [wrap-json-params]]
        [ring.middleware.format-response :only [wrap-json-response]])
  (:require [compojure [handler :as handler] [route :as route]]
            [pimy.storage :as storage]
            [pimy.utils.http :as http]))

(defroutes api-routes
  (context "/api" []
    (GET "/" [] {:body {:version (config :version )}})

    (POST "/records" {body :body-params} {:body (storage/create-record body)})
    (GET "/records" {params :query-params} {
                                             :headers {"X-Total-Count" (str (storage/records-count))}
                                             :body (storage/list-records params)
                                             })
    (GET "/records/:id" [id] (http/create-response (storage/read-record id)))
    (PUT "/records/:id" {{id :id} :params body :body-params} {:body (storage/update-record id body)})

    (ANY "/*" [] (not-found "not found")))

  (route/resources "/public")
  (GET "/*" [] (resource-response "/public/index.html")))

(def app
  (->
    (handler/api api-routes)

    (http/wrap-request-logger)
    (http/request-wrapper-keywordize)
    (http/wrap-exception-handler)
    (http/wrap-response-logger)

    (wrap-json-params)
    (wrap-json-response)
    ))
