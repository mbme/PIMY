(ns pimy.utils.helpers
  (:use [clojure.string :only [blank?]]
        [clojure.java.io :only [resource]]
        [clojure.walk :only [stringify-keys]])
  (:require [clojure.tools.logging :as log]
            [clj-time.core :as time]
            [clj-time.coerce :as time-conv]
            [clojure.edn :as edn])
  (:import [java.util HashMap]))

;; read config file
(def config (edn/read-string (slurp (resource "properties.edn"))))

(log/info "version" (:version config))

(def not-nil? (complement nil?))

(def not-blank? (complement blank?))

(defn now [] (time-conv/to-timestamp (time/now)))

(defn throw-IAE
  [& errs]
  (throw (IllegalArgumentException. (str errs))))

(defn to-java-map [map]
  (HashMap. (stringify-keys map)))

(defn remove-nil [map]
  (into {} (remove (comp nil? val) map)))

(defn str->long
  ([number] (str->long number nil))
  ([number def-val]
    (if (number? number)
      number
      (try
        (Long/parseLong number)
        (catch NumberFormatException e
          (if-not (nil? def-val)
            def-val
            (throw-IAE "bad number" number))
          )))
    ))
