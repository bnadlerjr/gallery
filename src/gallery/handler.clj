(ns gallery.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [noir.session :as session]
            [gallery.routes.home :refer [home-routes]]
            [gallery.routes.auth :refer [auth-routes]]
            [gallery.routes.upload :refer [upload-routes]]))

(defn init []
  (println "gallery is starting"))

(defn destroy []
  (println "gallery is shutting down"))

(defn user-page [_] (session/get :user))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler
           [auth-routes
            home-routes
            upload-routes
            app-routes]
           :access-rules [user-page]))
