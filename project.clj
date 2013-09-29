(defproject pimy "0.1.4"
  :description "PIM"
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [compojure "1.1.5"]
                 [ring-middleware-format "0.3.0"]

                 [org.clojure/tools.logging "0.2.4"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jdmk/jmxtools
                                                    com.sun.jmx/jmxri]]

                 [korma "0.3.0-RC5"]
                 [com.h2database/h2 "1.3.173"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler pimy.handler/app
         :auto-reload? true
         :auto-refresh? true}
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]}
             :test {:resource-paths ["test-resources"]}}
  :aliases {"serv" ["ring" "server-headless"]})
