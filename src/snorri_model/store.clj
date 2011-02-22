(ns snorri-model.store
  "Storage layer to Appengine's Datastore"
  (:require [appengine-magic.services.datastore :as ds]
            [clojure.string :as string]))

;; DataStore definitions
(ds/defentity Symbol [^:key symbol last-scrape-date last-scrape-result
                      success-count failure-count])
(ds/defentity Data [^:key date-symbol symbol date close pe es eg])

(defn normalize-symbol
  "Remove spaces and concert to uppercase."
  [symbol]
  (string/trim (string/upper-case symbol)))

(defn get-symbols
  "Return all symbols from the DS."
  []
  (ds/query :kind Symbol
            :sort [:symbol]
            :chunk-size 200))

(defn get-scrape-symbols
  "Return all symbols that are not yet scraped on the given date."
  [date]
  (ds/query :kind Symbol
            :filter (< :last-scrape-date date)
            :sort [:last-scrape-date :symbol]))

(defn create-symbol!
  "Store the new symbol to the DS."
  [symbol]
  (ds/save! (Symbol. (normalize-symbol symbol) nil nil 0 0)))

(defn delete-symbol!
  "Delete the symbol from the DS."
  [symbol]
  (ds/delete! (ds/query :kind Symbol
                        :filter (= :symbol (normalize-symbol symbol)))))

(defn symbol-exists?
  "Check if the symbol already exists in the DS."
  [symbol]
  (ds/exists? Symbol (normalize-symbol symbol)))

;; TODO room for improvement / cleanup here.
(defn update-symbol-stats!
  "Update the scrape statistics for the given symbol."
  [symbol date result]
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

(defn unfold-data
  "Convert the serialized list of PE and ES back to actual lists."
  [{:keys [pe es] :as data}]
  (assoc data :pe (string/split pe #" ")
              :es (string/split es #" ")))

(defn get-data
  "Return all stock data for the given date."
  [date]
  (map unfold-data (ds/query :kind Data
                             :filter (= :date date)
                             :sort [:symbol]
                             :chunk-size 200)))

;; XXX Performance issue?
(defn get-last-date
  "Return the most recent date we have stock data for."
  []
  (:date (first (ds/query :kind Data :sort [[:date :desc]] :limit 1))))

(defn add-data!
  "Add stock data to the DS. Lists are serialized into strings."
  [{:keys [symbol date close pe es eg]}]
  (ds/save! (Data. (str date "_" symbol) symbol date close
                   (string/join " " pe) (string/join " " es) eg)))

(defn data-exists?
  "Check if stock data exists for given date and symbol."
  [date symbol]
  (ds/exists? Data (str date "_" symbol)))
