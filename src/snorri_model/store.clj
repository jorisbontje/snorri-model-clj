(ns snorri-model.store
  (:require [appengine-magic.services.datastore :as ds]
            [clojure.string :as string]))

(ds/defentity Symbol [^:key symbol])
(ds/defentity Data [^:key date-symbol symbol date close pe es eg])

(defn normalize-symbol [symbol]
  (string/trim (string/upper-case symbol)))

(defn get-symbols []
  (ds/query :kind Symbol
            :sort [:symbol]))

(defn create-symbol! [symbol]
  (ds/save! (Symbol. (normalize-symbol symbol))))

(defn delete-symbol! [symbol]
  (ds/delete! (ds/query :kind Symbol
                        :filter (= :symbol (normalize-symbol symbol)))))

(defn symbol-exists? [symbol]
  (ds/exists? Symbol (normalize-symbol symbol)))

(defn unfold-data [{:keys [pe es] :as data}]
  (assoc data :pe (string/split pe #" ")
              :es (string/split es #" ")))

(defn get-data []
  (map unfold-data (ds/query :kind Data :sort [:symbol])))

(defn get-last-date []
  (:date (first (ds/query :kind Data :sort [[:date :desc]]))))

(defn add-data! [{:keys [symbol date close pe es eg]}]
  (ds/save! (Data. (str date "_" symbol) symbol date close
                   (string/join " " pe) (string/join " " es) eg)))

(defn data-exists? [date symbol]
  (ds/exists? Data (str date "_" symbol)))
