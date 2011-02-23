(ns snorri-model.core
  "The application starts here. This namespace defines the routes
  and dispatches GET and POST requests to the right handlers."
  (:require [appengine-magic.core :as ae]
            [snorri-model.admin :as admin]
            [snorri-model.api :as api]
            [snorri-model.middleware :as mw]
            [snorri-model.public :as public]
            [snorri-model.view :as view])
  (:use compojure.core
        ring.middleware.reload
        ring.middleware.params
        ring.util.response))

;; API tasks not ment to be called by humans.
(defroutes api-routes
  (GET "/cron/daily" []
    (api/daily-update))
  (POST "/tasks/fetch" [symbol]
    (api/fetch-symbol symbol)))

;; The public (home)page.
(defroutes public-routes
  (GET "/" []
    (public/index)))

;; Admin tasks, security is done via web.xml
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

;; Are we running in the REPL?
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
