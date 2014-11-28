(defproject gallery "0.1.0-SNAPSHOT"
  :description "Example picture gallery app from Web development w/ Clojure book."
  :url "https://github.com/bnadlerjr/gallery"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [lib-noir "0.7.6"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql/postgresql "9.1-901.jdbc4"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler gallery.handler/app
         :init gallery.handler/init
         :destroy gallery.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
