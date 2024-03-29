(defproject agilator-be "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [org.immutant/web "2.1.10"]
                 [ring/ring-core "1.9.6"]
                 [org.clojure/data.json "0.2.7"]
                 [ring/ring-devel "1.9.6"]
                 [clj-htmltopdf "0.2.1"]
                 [overtone/at-at "1.2.0"]]
  :plugins [[lein-ring "0.12.5"]]
  :main agilator-be.handler
  :aot [agilator-be.handler]
  :uberjar-name "agilator.jar"
  :ring {:handler agilator-be.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
