(ns snorri-model.public
  "Hander for public (home)page."
  (:require [appengine-magic.services.memcache :as memcache]
            [snorri-model.process :as process]
            [snorri-model.store :as store]
            [snorri-model.view :as view])
  (:import com.google.appengine.api.memcache.Expiration))

(defn get-cached-data
  "Get the data from cache if possible, otherwise calls content-fn and store."
  [cache-key content-fn cache-expiration]
  (if-let [cached-data (memcache/get cache-key)]
    cached-data
    (let [content (content-fn)]
      (memcache/put! cache-key content
                     :expiration (Expiration/byDeltaSeconds cache-expiration))
      content)))

(defn render-index-data
  "Renders the index data block."
  []
  (let [last-date (store/get-last-date)
        data (store/get-data last-date)
        enriched-data (map process/enrich-data data)]
    (view/index last-date enriched-data)))

(defn index
  "Index page."
  []
  (view/layout
    (get-cached-data "page-index" render-index-data 600)))
