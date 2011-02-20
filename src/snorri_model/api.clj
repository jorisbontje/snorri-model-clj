(ns snorri-model.api
  (:require [appengine-magic.services.task-queues :as tq]
            [clj-stacktrace.repl :as strp]
            [snorri-model.harvest :as harvest]
            [snorri-model.store :as store]
            [snorri-model.util :as util]))

(defn return-200 [message]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str message "\r\n")})

(defn daily-update []
  (let [today (util/today)]
    (println "Queueing symbols")
    (doseq [{symbol :symbol} (store/get-scrape-symbols today)]
      (tq/add! :url "/tasks/fetch" :queue "fetchqueue" :params {:symbol symbol}))
    (return-200 "OK")))

(defn fetch-symbol [symbol]
  (do (println "Fetching symbol" symbol)
    (if (store/symbol-exists? symbol)
      (do
        (try
          (harvest/harvest symbol)
          (catch Exception e
            (util/log "Exception:\n %s" (strp/pst-str e))))
        (return-200 "OK"))
      (return-200 "UNKNOWN SYMBOL"))))
