(defproject pimy "0.1.7"
  :description "PIM"
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [compojure "1.1.6"]
                 [ring-middleware-format "0.3.1"]

                 [org.clojure/tools.logging "0.2.4"]
                 [org.slf4j/slf4j-log4j12 "1.7.5"]
                 [log4j/log4j "1.2.17" :exclusions [
                                                     javax.mail/mail
                                                     javax.jms/jms
                                                     com.sun.jdmk/jmxtools
                                                     com.sun.jmx/jmxri]]

                 [metis "0.3.3"]

                 [com.j256.ormlite/ormlite-jdbc "4.47"]
                 [com.h2database/h2 "1.3.173"]]
  :plugins [[lein-ring "0.8.8"]
            [lein-kibit "0.0.8"]
            [lein-resource "0.3.2"]]
  :ring {:handler pimy.handler/app
         :auto-reload? true
         :auto-refresh? true}

  :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
  :java-source-paths ["src/java"]

  :source-paths ["src/clojure"]
  :profiles {:dev {:source-paths ["dev"] :dependencies [[ring-mock "0.1.5"]]}
             :test {:resource-paths ["test-resources"]}}
  :aliases {"serv" ["ring" "server-headless"]}

  :resource {:resource-paths ["templates"]
             :target-path "resources"}
  :hooks [leiningen.resource]
  )
