(ns snorri-model.admin
  (:require [appengine-magic.services.user :as user]
            [snorri-model.store :as store]
            [snorri-model.view :as view])
  (:use ring.util.response))

(defn index []
  (if (not (and (user/user-logged-in?) (user/user-admin?)))
    (redirect "/")
    (view/layout 
      (view/symbols (store/get-symbols)))))

(defn add-symbol [symbol]
  (if (not (and (user/user-logged-in?) (user/user-admin?)))
    (redirect "/")
    (do
      (store/create-symbol! symbol)
      (redirect "/symbols/"))))

(defn delete-symbol [symbol]
  (if (not (and (user/user-logged-in?) (user/user-admin?)))
    (redirect "/")
    (do
      (store/delete-symbol! symbol)
      (redirect "/symbols/"))))
