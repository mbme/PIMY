(defproject pimy "0.1.3"
  :description "I'm sure it's my personal PIM"
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [compojure "1.1.5"]
                 [ring-middleware-format "0.3.0"]

                 [org.clojure/tools.logging "0.2.4"]
                 [log4j/log4j "1.2.17"]

                 [org.clojure/java.jdbc "0.3.0-alpha4"]
                 [com.mchange/c3p0 "0.9.2.1"]
                 [com.h2database/h2 "1.3.173"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler pimy.handler/app
         :auto-reload? true
         :auto-refresh? true}
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]}
             :test {:resource-paths ["test-resources"]}})
