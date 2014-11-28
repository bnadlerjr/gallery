(ns gallery.routes.home
  (:require [compojure.core :refer :all]
            [gallery.views.layout :as layout]
            [noir.session :as session]
            [gallery.routes.gallery :refer [show-galleries]]))

(defn home []
  (layout/common (show-galleries)))

(defroutes home-routes
  (GET "/" [] (home)))
