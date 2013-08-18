(defproject pimy "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring-middleware-format "0.3.0"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler pimy.handler/app
         :auto-reload? true
         :auto-refresh? true
         :open-browser? false}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
