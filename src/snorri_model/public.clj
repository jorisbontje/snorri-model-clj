(ns snorri-model.public
  "Hander for public (home)page."
  (:require [snorri-model.process :as process]
            [snorri-model.store :as store]
            [snorri-model.view :as view]))

(defn index []
  (let [last-date (store/get-last-date)
        data (store/get-data last-date)
        enriched-data (map process/enrich-data data)]
    (view/layout
      (view/index last-date enriched-data))))

