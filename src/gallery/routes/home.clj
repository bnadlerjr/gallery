(ns gallery.routes.home
  (:require [compojure.core :refer :all]
            [gallery.views.layout :as layout]
            [noir.session :as session]))

(defn home []
  (layout/common [:h1 "Hello " (session/get :user)]))

(defroutes home-routes
  (GET "/" [] (home)))
