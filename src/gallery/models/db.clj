(ns gallery.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "db.sq3"})

(defn create-user [user]
  (sql/with-connection
    db
    (sql/insert-record :users user)))
