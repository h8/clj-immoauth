(ns clj-immoauth.core
  (:require [immutant.web :as web]
            [compojure.core :refer [GET defroutes wrap-routes]]
            [compojure.route :as route]
            [ring.util.response :refer [response redirect]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.codec :refer [form-encode]]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [nomad :refer [defconfig]])
  (:gen-class))

(defconfig app-config (clojure.java.io/resource "config.edn"))

(defn counter [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (-> (response (str "Counter is " count))
        (assoc :session session))))

(defn get-user [{{user :user} :session}] user)

(defn get-oauth-url []
  (let [params (form-encode {:redirect_uri "http://localhost:8080/oauth"
                             :response_type "code"
                             :client_id (get-in (app-config) [:client_id])
                             :scope "email profile"})]
    (str "https://accounts.google.com/o/oauth2/auth?" params)))

(defn get-oauth-access-token [code]
  (let [client-id (get-in (app-config) [:client_id])
        client-secret (get-in (app-config) [:client_secret])
        {body :body} (client/post "https://www.googleapis.com/oauth2/v3/token"
                                  {:form-params {:code code
                                                 :client_id client-id
                                                 :client_secret client-secret
                                                 :redirect_uri "http://localhost:8080/oauth"
                                                 :grant_type "authorization_code"}})]
    ((json/read-str body) "access_token")))

(defn get-oauth-user-info [token]
  (let [{body :body} (client/get "https://www.googleapis.com/oauth2/v2/userinfo"
                                 {:headers {"Authorization" (str "Bearer " token)}})]
    (json/read-str body)))

(defn login [request]
  (if-let [user (get-user request)]
    (redirect "/profile")
    (redirect (get-oauth-url))))

(defn logout [request]
  (-> (redirect "/")
      (assoc :session nil)))

(defn oauth-callback [{{code "code"} :params session :session}]
  (let [token (get-oauth-access-token code)
        user-info (get-oauth-user-info token)
        redirect-uri (:redirect-uri session "/")
        updated-session (-> session
                            (assoc :user (user-info "email"))
                            (dissoc :redirect-uri))]
    (-> (redirect redirect-uri)
        (assoc :session updated-session))))

(defn user-profile [request]
  (let [user (get-user request)]
    (response (str "User profile of " user))))

(defn wrap-auth [app]
  (fn [{session :sesion uri :uri :as request}]
    (if-let [user (get-user request)]
      (app request)
      (let [updated-session (assoc session :redirect-uri uri)]
        (-> (redirect "/login")
            (assoc :session updated-session))))))

(defroutes protected
  (GET "/profile" _ user-profile)
  (GET "/private" _ (response "Private page")))

(defroutes routes
  (GET "/" _ (response "Hello world!"))
  (GET "/counter" _ counter)
  (GET "/login" _ login)
  (GET "/oauth" _ oauth-callback)
  (GET "/logout" _ logout)
  (wrap-routes protected wrap-auth)
  (route/not-found "404"))

(def wrapped-routes
  (-> routes
      (immutant.web.middleware/wrap-session)
      (wrap-params)))

(defn -main []
  (web/run wrapped-routes))

(defn run-dev-server []
  (web/run-dmc wrapped-routes))

(defn stop []
  (web/stop))

