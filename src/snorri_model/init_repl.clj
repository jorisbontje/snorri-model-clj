(ns user)
(use 'clojure.stacktrace)
(use '[clojure.contrib.repl-utils :only (show)]) 

(require '[appengine-magic.core :as ae])
(use '[snorri-model.core])

(defn reload! []
  (require 'snorri-model.core :reload-all))

(ae/serve snorri-model-app)
(println "Interactive Jetty instance started. To force a reload: (reload!)")
