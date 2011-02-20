(ns snorri-model.harvest
  (:require [appengine-magic.services.url-fetch :as uf]
            [snorri-model.scrape :as scrape]
            [snorri-model.store :as store]
            [snorri-model.util :as util]))

(defn fetch-symbol [symbol]
  (let [url (scrape/get-scrape-url symbol)]
    (uf/fetch url)))

(defn fetch-success? [response]
  (= 200 (:response-code response)))

(defn log-fetch-failure [symbol]
  (println "Error fetching symbol" symbol))

(defn log-parse-failure [symbol]
  (println "Error parsing html" symbol))

(defn store-data! [symbol data]
  (let [today (util/today)]
    (store/add-data! (assoc data :symbol symbol :date today))))

(defn process-response [symbol response]
  (let [html (String. (:content response))
        data (scrape/extract-data html)]
    (if data
      (store-data! symbol data)
      (log-parse-failure symbol))))

(defn harvest [symbol]
  (let [response (fetch-symbol symbol)]
    (if (fetch-success? response)
      (process-response symbol response)
      (log-fetch-failure symbol))))
