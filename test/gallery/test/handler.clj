(ns gallery.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [noir.util.crypt :refer [encrypt]]
            [gallery.handler :refer :all]))

(defn mock-get-user [id]
  (if (= "foo" id)
    {:id "foo" :pass (encrypt "12345")}))

(deftest test-login
  (testing "login success"
    (with-redefs [gallery.models.db/get-user mock-get-user]
      (is
        (-> (request :post "/login" {:id "foo" :pass "12345"})
            app :headers (get "Set-Cookie") not-empty))))
  (testing "password mismatch"
    (with-redefs [gallery.models.db/get-user mock-get-user]
      (is
        (-> (request :post "/login" {:id "foo" :pass "123456"})
            app :headers (get "Set-Cookie") empty?))))
  (testing "user not found"
    (with-redefs [gallery.models.db/get-user mock-get-user]
      (is
        (-> (request :post "/login" {:id "bar" :pass "12345"})
            app :headers (get "Set-Cookie") empty?)))))
