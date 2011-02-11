(ns snorri-model.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use snorri-model.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method snorri-model-app) this request response))
