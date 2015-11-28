(defproject clj-immoauth "0.1.0-SNAPSHOT"
  :description "Simple demo application which uses Immutant"
  :url "https://github.com/h8/clj-immoauth"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.immutant/web "2.1.1"]
                 [compojure "1.4.0"]
                 [clj-http "2.0.0"]
                 [org.clojure/data.json "0.2.6"]
                 [jarohen/nomad "0.7.2"]]
  :profiles {:repl {:plugins [[org.clojure/tools.namespace "0.2.4"]
                              [cider/cider-nrepl "0.9.1"]]
                    :dependencies [[ring/ring-devel "1.4.0"]]}}
  :main clj-immoauth.core)
