(ns snorri-model.public
  "Hander for public (home)page."
  (:require [appengine-magic.services.memcache :as memcache]
            [snorri-model.process :as process]
            [snorri-model.store :as store]
            [snorri-model.view :as view])
  (:import com.google.appengine.api.memcache.Expiration))

(def index-cache-key "page-index")
(def cache-expiration (Expiration/byDeltaSeconds 600))

(defn index-data
  "Get the index data from cache if possible."
  []
  (if-let [cached-data (memcache/get index-cache-key)]
    cached-data
    (let [last-date (store/get-last-date)
          data (store/get-data last-date)
          enriched-data (map process/enrich-data data)
          rendered-content (view/index last-date enriched-data)]
      (memcache/put! index-cache-key rendered-content :expiration cache-expiration)
      rendered-content)))

(defn index
  "Index page."
  []
  (view/layout
    (index-data)))

