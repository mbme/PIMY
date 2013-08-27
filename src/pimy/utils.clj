(ns pimy.utils
  (:use compojure.core
        ring.util.response
        [cheshire.custom :only [JSONable]]
        [clojure.string :only [upper-case]]
        [clojure.java.io :only [resource]])
  (:require [clojure.tools.logging :as log]
            [clojure.edn :as edn])
  (:import (com.fasterxml.jackson.core JsonGenerator)))

(def config (edn/read-string (slurp (resource "properties.edn"))))
(log/info "version" (:version config))

(defn wrap-exception-handler
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (->
          (response e)
          (status 500))))))

(defn wrap-request-logger [handler]
  (fn [req]
    (let [{remote-addr :remote-addr request-method :request-method uri :uri} req]
      (log/debug remote-addr (upper-case (name request-method)) uri)
      (handler req))))

(defn wrap-response-logger
  [handler]
  (fn [req]
    (let [response (handler req)
          {remote-addr :remote-addr request-method :request-method uri :uri} req
          {status :status body :body} response]
      (if (instance? Exception body)
        (log/warn body remote-addr (upper-case (name request-method)) uri "->" status body)
        (log/debug remote-addr (upper-case (name request-method)) uri "->" status))
      response)))

(extend java.lang.Exception
  JSONable
  {:to-json (fn [^Exception e ^JsonGenerator jg]
              (.writeStartObject jg)
              (.writeFieldName jg "exception")
              (.writeString jg (.getName (class e)))
              (.writeFieldName jg "message")
              (.writeString jg (.getMessage e))
              (.writeEndObject jg))})

(def not-nil? (complement nil?))

(defn check-key [key m validators]
  (if (contains? validators key)
    (if (nil? (m key))
      {:msg "Required field is nil" :key key :val (m key)})
    (if (not-nil? (m key))
      {:msg "Unexpected field" :key key :val (m key)})))

(defn check-validity [m required-fields]
  (filter not-nil? (map #(check-key % m required-fields) (keys m))))
