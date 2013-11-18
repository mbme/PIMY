(ns pimy.utils.http
  (:use ring.util.response
        compojure.core
        [clojure.walk :only [keywordize-keys]]
        [cheshire.custom :only [JSONable]]
        [clojure.string :only [upper-case join]])
  (:require [clojure.tools.logging :as log])
  (:import (com.fasterxml.jackson.core JsonGenerator)
           [org.joda.time DateTime]))

(defn options
  "builds 'Allow' header for available http methods"
  ([] (options #{:options } nil))
  ([allowed] (options allowed nil))
  ([allowed body]
    (header
      (response body)
      "Allow"
      (join ", " (map (comp upper-case name) allowed)))
    ))

(defn method-not-allowed
  "builds '405 not allowed' response"
  [allowed]
  (status (options allowed) 405))

(defn no-content? [body]
  (if (or (nil? body) (empty? body))
    (status (response nil) 204)
    (response body)
    ))

(defn create-response
  [body]
  (if (nil? body)
    (status (response nil) 404)
    (status (response body) 200)
    ))


(defn not-implemented []
  (status (response nil) 501))

(defn wrap-exception-handler [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable e
        (status (response e) 500)
        ))
    ))

(defn wrap-request-logger [handler]
  (fn [req]
    (let [{remote-addr :remote-addr request-method :request-method uri :uri} req]
      (log/debug remote-addr (upper-case (name request-method)) uri)
      (handler req))))

(defn wrap-response-logger [handler]
  (fn [req]
    (let [response (handler req)
          {remote-addr :remote-addr request-method :request-method uri :uri} req
          {status :status body :body} response]
      (if (instance? Exception body)
        (log/warn body remote-addr (upper-case (name request-method)) uri "->" status body)
        (log/debug remote-addr (upper-case (name request-method)) uri "->" status))
      response)))

(defn request-wrapper-keywordize [handler]
  (fn [req]
    (-> req
      (assoc :query-params (keywordize-keys (req :query-params )))
      (assoc :body-params (keywordize-keys (req :body-params )))
      (handler))
    ))

(extend java.lang.Exception
  JSONable
  {:to-json (fn [^Exception e ^JsonGenerator jg]
              (.writeStartObject jg)
              (.writeFieldName jg "exception")
              (.writeString jg (.getName (class e)))
              (.writeFieldName jg "message")
              (.writeString jg (.getMessage e))
              (.writeEndObject jg))})

(extend org.joda.time.DateTime
  JSONable
  {:to-json (fn [^DateTime dt ^JsonGenerator jg]
              (.writeNumber jg (.getMillis dt))
              )})