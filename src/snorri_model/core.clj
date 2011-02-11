(ns snorri-model.core
  (:require [appengine-magic.core :as ae]
            [snorri-model.middleware :as mw])
  (:use compojure.core
        ring.middleware.reload))

(defroutes snorri-model-app-handler
  (GET "/" req
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body "Hello, Xebia"})
  (ANY "*" _
       {:status 404
        :headers {"Content-Type" "text/plain"}
        :body "not found"}))

(def interactive?
  (= :interactive (ae/appengine-environment-type)))

(def app
  (-> #'snorri-model-app-handler
    (mw/wrap-if interactive? wrap-reload '[snorri-model.core])))

(ae/def-appengine-app snorri-model-app #'app)
