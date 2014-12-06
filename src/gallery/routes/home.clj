(ns gallery.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [gallery.views.layout :as layout]
            [gallery.util :refer [thumb-prefix]]
            [gallery.models.db :as db]
            [noir.session :as session]))

(defn home []
  (layout/render "home.html"
                 {:thumb-prefix thumb-prefix
                  :galleries (db/get-gallery-previews)}))

(defroutes home-routes
  (GET "/" [] (home)))
