(ns snorri-model.api
  (:require [appengine-magic.services.task-queues :as tq]
            [snorri-model.harvest :as harvest]
            [snorri-model.store :as store]))

(defn return-200 [message]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str message "\r\n")})

(defn daily-update []
  (do
    (doseq [{symbol :symbol} (store/get-symbols)]
      (println "Queueing " symbol)
      (tq/add! :url "/tasks/fetch" :params {:symbol symbol}))
  (return-200 "OK")))

(defn fetch-symbol [symbol]
  (do (println "Fetching symbol: " symbol)
    (if (store/symbol-exists? symbol)
      (do
        (harvest/harvest symbol)
        (return-200 "OK"))
      (return-200 "UNKNOWN SYMBOL"))))
