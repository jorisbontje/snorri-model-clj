(ns snorri-model.harvest
  "Harvest the stock ticker."
  (:require [appengine-magic.services.url-fetch :as uf]
            [snorri-model.scrape :as scrape]
            [snorri-model.store :as store]
            [snorri-model.util :as util]))

(defn fetch-symbol
  "Fetch the html with stock data for the given symbol."
  [symbol]
  (let [url (scrape/get-scrape-url symbol)]
    (uf/fetch url)))

(defn fetch-success?
  "Check if fetch was successfull."
  [response]
  (= 200 (:response-code response)))

(defn log-failure
  "Log failure and update stats to indicate failure."
  [error symbol date]
  (util/log error symbol)
  (store/update-symbol-stats! symbol date :failure))

(defn store-data!
  "Store stock data to the datastore and update stats to indicate success."
  [symbol date data]
  (store/add-data! (assoc data :symbol symbol :date date))
  (store/update-symbol-stats! symbol date :success))

(defn process-response
  "Process (scrape, extract and store) the stock data."
  [symbol date response]
  (let [html (String. (:content response))
        data (scrape/extract-data html)]
    (if data
      (store-data! symbol date data)
      (log-failure "Error parsing html %s" symbol date))))

(defn harvest
  "Harvest the stock identified by the given symbol."
  [symbol]
  (let [response (fetch-symbol symbol)
        date (util/today)]
    (if (fetch-success? response)
      (process-response symbol date response)
      (log-failure "Error fetching html %s" symbol date))))
