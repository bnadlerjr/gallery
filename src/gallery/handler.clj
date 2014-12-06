(ns gallery.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [noir.session :as session]
            [taoensso.timbre :as timbre]
            [com.postspectacular.rotor :as rotor]
            [gallery.routes.home :refer [home-routes]]
            [gallery.routes.auth :refer [auth-routes]]
            [gallery.routes.upload :refer [upload-routes]]
            [gallery.routes.gallery :refer [gallery-routes]]))

(defn init []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false
     :max-message-per-msecs nil
     :fn rotor/append})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "error.log" :max-size (* 512 1024) :backlog 10})

  (timbre/info "gallery started successfully"))

(defn destroy []
  (timbre/info "gallery is shutting down"))

(defn user-page [_] (session/get :user))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler
           [auth-routes
            home-routes
            upload-routes
            gallery-routes
            app-routes]
           :access-rules [user-page]))
