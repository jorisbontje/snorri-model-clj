(ns snorri-model.api
  "This namespace contains API tasks such as cron and task queue calls."
  (:require [appengine-magic.services.task-queues :as tq]
            [clj-stacktrace.repl :as strp]
            [snorri-model.harvest :as harvest]
            [snorri-model.store :as store]
            [snorri-model.util :as util]))

(defn return-status [code message]
  {:status code
   :headers {"Content-Type" "text/plain"}
   :body (str message "\r\n")})

(defn daily-update
  "Perform the daily update, queue up all the symbols that haven't been scraped
  already for today."
  []
  (let [today (util/today)]
    (println "Queueing symbols")
    (doseq [{symbol :symbol} (store/get-scrape-symbols today)]
      (tq/add! :url "/tasks/fetch" :queue "fetchqueue" :params {:symbol symbol}))
    (return-status 200 "OK")))

(defn fetch-symbol
  "Fetch and process the given symbol. For unknown errors returns success, to prevent
  endless requeueing in case of scrape/parse errors."
  [symbol]
  (do (println "Fetching symbol" symbol)
    (if (store/symbol-exists? symbol)
      (do
        (try
          (harvest/harvest symbol)
          (return-status 200 "OK")
          (catch java.io.IOException e
            (util/log "IOException:\n %s" (strp/pst-str e))
            (return-status 504 "IOERROR"))
          (catch Exception e
            (util/log "Exception:\n %s" (strp/pst-str e)))))
      (return-status 200 "UNKNOWN SYMBOL"))))
