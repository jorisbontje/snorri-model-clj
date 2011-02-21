(ns user
  "Init script for Leiningen REPL."
  (:require [appengine-magic.core :as ae])
  (:use [clojure.stacktrace]
        [clojure.contrib.repl-utils :only (show)]
        [snorri-model.core]))

(defn reload! []
  (require 'snorri-model.core :reload-all))

(ae/serve snorri-model-app)
(println "Interactive Jetty instance started. To force a reload: (reload!)")
