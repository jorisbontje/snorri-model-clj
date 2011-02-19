(ns snorri-model.store
  (:require [appengine-magic.services.datastore :as ds]
            [clojure.string :as string]))

(ds/defentity Symbol [^:key symbol])
(ds/defentity Data [^:key date-symbol symbol date close
                    pe0 pe1 pe2 pe3 pe4 pe5 pe6 pe7 pe8 pe9
                    es0 es1 es2 es3 eg])

(defn get-symbols []
  (ds/query :kind Symbol
            :sort [:symbol]))

(defn create-symbol! [symbol]
  (ds/save! (Symbol. (string/trim
                       (string/upper-case symbol)))))

(defn delete-symbol! [symbol]
  (ds/delete! (ds/query :kind Symbol :filter (= :symbol symbol))))

(defn unfold-data [{:keys [pe0 pe1 pe2 pe3 pe4 pe5 pe6 pe7 pe8 pe9
                           es0 es1 es2 es3] :as data}]
  (assoc data :pe [pe0 pe1 pe2 pe3 pe4 pe5 pe6 pe7 pe8 pe9]
              :es [es0 es1 es2 es3]))

(defn get-data []
  (map unfold-data (ds/query :kind Data :sort [:symbol])))

(defn get-last-date []
  (:date (first (ds/query :kind Data :sort [[:date :desc]]))))

(defn add-data! [{:keys [symbol date close pe es eg]}]
  (let [[pe0 pe1 pe2 pe3 pe4 pe5 pe6 pe7 pe8 pe9] pe
        [es0 es1 es2 es3] es]
    (ds/save! (Data. (str date "_" symbol) symbol date close
                     pe0 pe1 pe2 pe3 pe4 pe5 pe6 pe7 pe8 pe9
                     es0 es1 es2 es3 eg))))
