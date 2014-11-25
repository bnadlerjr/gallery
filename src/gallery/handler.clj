(ns gallery.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [gallery.routes.home :refer [home-routes]]))

(defn init []
  (println "gallery is starting"))

(defn destroy []
  (println "gallery is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler [home-routes app-routes]))
