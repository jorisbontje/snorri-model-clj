(ns snorri-model.store
  "Storage layer to Appengine's Datastore"
  (:require [appengine-magic.services.datastore :as ds]
            [clojure.string :as string]))

(ds/defentity Symbol [^:key symbol last-scrape-date last-scrape-result
                      success-count failure-count])
(ds/defentity Data [^:key date-symbol symbol date close pe es eg])

(defn normalize-symbol [symbol]
  (string/trim (string/upper-case symbol)))

(defn get-symbols []
  (ds/query :kind Symbol
            :sort [:symbol]))

(defn get-scrape-symbols [date]
  (ds/query :kind Symbol
            :filter (< :last-scrape-date date)
            :sort [:last-scrape-date :symbol]))

(defn create-symbol! [symbol]
  (ds/save! (Symbol. (normalize-symbol symbol) nil nil 0 0)))

(defn delete-symbol! [symbol]
  (ds/delete! (ds/query :kind Symbol
                        :filter (= :symbol (normalize-symbol symbol)))))

(defn symbol-exists? [symbol]
  (ds/exists? Symbol (normalize-symbol symbol)))

(defn update-symbol-stats! [symbol date result]
  (ds/with-transaction
    (let [curr (ds/retrieve Symbol symbol)
          success-count (or (:success-count curr) 0)
          failure-count (or (:failure-count curr) 0)]
      (ds/save! (assoc curr :last-scrape-date date
                            :last-scrape-result (name result)
                            :success-count (if (= result :success)
                                             (inc success-count)
                                             success-count)
                            :failure-count (if (not= result :success)
                                             (inc failure-count)
                                             failure-count))))))

(defn unfold-data [{:keys [pe es] :as data}]
  (assoc data :pe (string/split pe #" ")
              :es (string/split es #" ")))

(defn get-data [date]
  (map unfold-data (ds/query :kind Data
                             :filter (= :date date)
                             :sort [:symbol])))

(defn get-last-date []
  (:date (first (ds/query :kind Data :sort [[:date :desc]]))))

(defn add-data! [{:keys [symbol date close pe es eg]}]
  (ds/save! (Data. (str date "_" symbol) symbol date close
                   (string/join " " pe) (string/join " " es) eg)))

(defn data-exists? [date symbol]
  (ds/exists? Data (str date "_" symbol)))
