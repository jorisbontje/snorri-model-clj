(ns snorri-model.harvest
  (:require [appengine-magic.services.url-fetch :as uf]
            [snorri-model.process :as process]
            [snorri-model.scrape :as scrape]
            [snorri-model.store :as store]
            [snorri-model.util :as util]))

(defn fetch-symbol [symbol]
  (let [url (scrape/get-scrape-url symbol)]
    (uf/fetch url)))

(defn fetch-success? [response]
  (= 200 (:response-code response)))

(defn log-fetch-failure [symbol response]
  (println "Error fetching symbol" symbol response))

(defn log-parse-failure [symbol html]
  (println "Error parsing html" symbol html))

(defn extract-data [html]
  (let [close (scrape/extract-close html)
        ten-y-pe (scrape/extract-avg10yPE html)
        es (scrape/extract-1yES html)
        eg (scrape/extract-5yEG html)]
    (when (and close ten-y-pe es eg)
        {:close close :pe (process/calc-avg10yPE ten-y-pe) :es es :eg eg})))

(defn store-data! [symbol data]
  (let [now (util/now)]
    (store/add-data! (assoc data :symbol symbol :date now))))

(defn process-response [symbol response]
  (let [html (String. (:content response))
        data (extract-data symbol html)]
    (if (data)
      (store-data! symbol data)
      (log-parse-failure symbol html))))

(defn harvest [symbol]
  (let [response (fetch-symbol symbol)]
    (if (fetch-success? response)
      (process-response symbol response)
      (log-fetch-failure symbol response))))
