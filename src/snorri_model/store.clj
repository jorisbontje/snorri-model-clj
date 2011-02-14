(ns snorri-model.store
  (:require [appengine-magic.services.datastore :as ds]
            [clojure.string :as string]))

(ds/defentity Symbol [^:key symbol])
(ds/defentity Data [^:key date-symbol symbol date close pe es eg])

(defn get-symbols []
  (ds/query :kind Symbol
            :sort [:symbol]))

(defn create-symbol [symbol]
  (ds/save! (Symbol. (string/trim
                       (string/upper-case symbol)))))

(defn delete-symbol [symbol]
  (ds/delete! (ds/query :kind Symbol :filter (= :symbol symbol))))

(defn get-data []
  (ds/query :kind Data :sort [:symbol]))

(defn get-last-date []
  (:date (first (ds/query :kind Data :sort [[:date :desc]]))))

(defn add-data [symbol date close pe es eg]
  (ds/save! (Data. (str date "_" symbol) symbol date close pe es eg)))
