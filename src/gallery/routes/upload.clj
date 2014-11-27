(ns gallery.routes.upload
  (:import (java.io File)
           (java.awt.geom AffineTransform)
           (java.awt.image AffineTransformOp BufferedImage)
           (javax.imageio ImageIO))
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [gallery.views.layout :as layout]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [gallery.models.db :as db]
            [gallery.util :refer [galleries gallery-path thumb-prefix thumb-uri]]))

(def thumb-size 150)

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance
                (double ratio) (double ratio))
        transform-op (AffineTransformOp.
                       scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let [img (ImageIO/read file)
        img-width (.getWidth img)
        img-height (.getHeight img)
        ratio (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
      (scale-image (io/input-stream (str path filename)))
      "jpeg"
      (File. (str path thumb-prefix filename)))))

(defn serve-file [user-id file-name]
  (file-response (str galleries File/separator user-id File/separator file-name)))

(defn upload-page [info]
  (layout/common
    [:h2 "upload an image"]
    [:p info]
    (form-to {:enctype "multipart/form-data"}
             [:post "/upload"]
             (file-upload :file)
             (submit-button "upload"))))

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page
    (if (empty? filename)
      "please select a file to upload"

      (try
        (noir.io/upload-file (gallery-path) file :create-path? true)
        (db/add-image (session/get :user) filename)
        (save-thumbnail file)
        (image {:height "150px"}
               (thumb-uri (session/get :user) filename))
        (catch Exception ex
          (str "error uploading file " (.getMessage ex)))))))

(defroutes upload-routes
           (GET "/upload" [info]
                (restricted (upload-page info)))

           (POST "/upload" [file]
                 (restricted (handle-upload file)))

           (GET "/img/:user-id/:file-name" [user-id file-name]
                (serve-file user-id file-name)))