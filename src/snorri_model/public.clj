(ns snorri-model.public
  (:require [snorri-model.process :as process]
            [snorri-model.store :as store]
            [snorri-model.view :as view]))

(defn index []
  (let [data (store/get-data)
        enriched-data (map process/enrich-data data)]
    (view/layout
      (view/index enriched-data))))

