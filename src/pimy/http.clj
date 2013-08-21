(ns pimy.http
  (:use ring.util.response
        [clojure.string :only [upper-case join]]))

(defn options
  "builds 'Allow' header for available http methods"
  ([] (options #{:options } nil))
  ([allowed] (options allowed nil))
  ([allowed body]
    (->
      (response body)
      (header "Allow" (join ", " (map (comp upper-case name) allowed))))))

(defn method-not-allowed
  "builds '405 not allowed' response"
  [allowed]
  (->
    (options allowed)
    (status 405)))

(defn no-content?
  [body]
  (if (or (nil? body) (empty? body))
    (->
      (response nil)
      (status 204))
    (response body)))

(defn not-implemented
  []
  (->
    (response nil)
    (status 501)))

(defn created
  ([url]
    (created url nil))
  ([url body]
    (->
      (response body)
      (status 201)
      (header "Location" url))))
