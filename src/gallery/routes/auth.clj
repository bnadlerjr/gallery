(ns gallery.routes.auth
  (:import (java.sql SQLException)
           (java.io File))
  (:require [compojure.core :refer :all]
            [gallery.routes.home :refer :all]
            [gallery.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [gallery.models.db :as db]
            [gallery.util :refer [gallery-path]]))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user ID is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn format-error [id ex]
  (cond
    (and (instance? SQLException ex)
         (= 0 (.getErrorCode ex)))
    (str "The user with ID " id " already exists!")
    :else
    "An error has occurred while processing the request."))

(defn registration-page [& [id]]
  (layout/render "registration.html"
                 {:id id
                  :id-error (first (vali/get-errors :id))
                  :pass-error (first (vali/get-errors :pass))}))

(defn create-gallery-path []
  (let [user-path (File. (gallery-path))]
    (if-not (.exists user-path) (.mkdirs user-path))
    (str (.getAbsolutePath user-path) File/separator)))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (db/create-user {:id id :pass (crypt/encrypt pass)})
      (session/put! :user id)
      (create-gallery-path)
      (resp/redirect "/")
      (catch Exception ex
        (vali/rule false [:id (format-error id ex)])
        (registration-page)))
    (registration-page id)))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare pass (:pass user)))
      (session/put! :user id)))
  (resp/redirect "/"))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
           (GET "/register" [] (registration-page))

           (POST "/register" [id pass pass1]
                 (handle-registration id pass pass1))

           (POST "/login" [id pass]
                 (handle-login id pass))

           (GET "/logout" []
                (handle-logout)))
