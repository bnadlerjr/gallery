(ns gallery.views.layout
  (:require [selmer.parser :as parser]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]))

(def template-folder "gallery/views/templates/")

(defn utf-8-response [html]
  (content-type (response html) "text/html; charset=utf-8"))

(deftype RenderablePage [content]
  Renderable
  (render [this request]
    (->> (assoc params
                :context (:context request)
                :user (session/get :user))
         (parser/render-file (str template-folder template))
         utf-8-response)))

(defn render [template & [params]]
  (RenderablePage. template params))

(defn make-menu [& items]
  [:div#usermenu (for [item items] [:div.menuitem item])])

(defn guest-menu []
  (make-menu
    (link-to "/" "home")
    (link-to "/register" "register")
    (form-to [:post "/login"]
             (text-field {:placeholder "screen name"} "id")
             (password-field {:placeholder "password"} "pass")
             (submit-button "login"))))

(defn user-menu [user]
  (make-menu
    (link-to "/" "home")
    (link-to "/upload" "upload images")
    (link-to "/logout" (str "logout " user))))

(defn base [& content]
  (RenderablePage. content))

(defn common [& content]
  (base
    (if-let [user (session/get :user)]
      (user-menu user)
      (guest-menu))
    [:div.content content]))
