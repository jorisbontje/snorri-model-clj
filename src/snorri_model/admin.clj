(ns snorri-model.admin
  "This namespace contains everything admin related."
  (:require [snorri-model.store :as store]
            [snorri-model.view :as view])
  (:use ring.util.response))

(defn index
  "Symbols index page."
  []
  (view/layout
    (view/symbols (store/get-symbols))))

(defn add-symbol
  "Add a new symbol if it doesn't already exist."
  [symbol]
  (do
    (when-not (store/symbol-exists? symbol)
      (store/create-symbol! symbol))
    (redirect "/symbols/")))

(defn delete-symbol
  "Delete a symbol."
  [symbol]
  (do
    (store/delete-symbol! symbol)
    (redirect "/symbols/")))
