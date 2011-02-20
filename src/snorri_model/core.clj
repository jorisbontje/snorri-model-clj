(ns snorri-model.core
  (:require [appengine-magic.core :as ae]
            [snorri-model.admin :as admin]
            [snorri-model.api :as api]
            [snorri-model.middleware :as mw]
            [snorri-model.public :as public])
  (:use compojure.core
        ring.middleware.reload
        ring.middleware.params
        ring.util.response))

(defroutes api-routes
  (GET "/cron/daily" []
    (api/daily-update))
  (POST "/tasks/fetch" [symbol]
    (api/fetch-symbol symbol)))

(defroutes public-routes
  (GET "/" []
    (public/index)))

(defroutes admin-routes
  (GET "/symbols/" []
    (admin/index))
  (POST "/symbols/" [symbol]
    (admin/add-symbol symbol))
  (POST "/symbols/:symbol" [symbol]
    (admin/delete-symbol symbol)))

(defroutes error-routes
  (ANY "/*" _
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "not found"}))

(defroutes dynamic-routes
  api-routes
  public-routes
  admin-routes
  error-routes)

(def interactive?
  (= :interactive (ae/appengine-environment-type)))

(def app
  (->
    #'dynamic-routes
    (wrap-params)
    (mw/wrap-if interactive? wrap-reload '[snorri-model.admin snorri-model.api
                                           snorri-model.core snorri-model.public
                                           snorri-model.view])))

(ae/def-appengine-app snorri-model-app #'app)
