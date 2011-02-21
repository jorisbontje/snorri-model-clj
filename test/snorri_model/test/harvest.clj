(ns snorri-model.test.harvest
  (:require [appengine-magic.services.url-fetch :as uf])
  (:use [clojure.contrib.string :only [substring?]]
        [clojure.test :only [deftest is]]
        [clojure.contrib.mock]
        [snorri-model.harvest]
        [snorri-model.scrape :as scrape]
        [snorri-model.util :as util]))

(deftest fetch-symbol-should-fetch-url-and-return-success
  (expect [uf/fetch
            (has-args [#(substring? "ABC" %)]
              (returns {:response-code 200}))]
    (is (= {:response-code 200} (fetch-symbol "ABC")))))

(deftest fetch-success?-should-check-success
  (is (fetch-success? {:response-code 200})))

(deftest process-response-should-store-success
  (expect [scrape/extract-data
             (has-args ["<html></html>"]
                (returns {:close "49.44"}))
           store-data!
             (has-args ["ABC" "2012-02-18" {:close "49.44"}])]
    (process-response "ABC" "2012-02-18" {:content "<html></html>"})))

(deftest process-response-should-log-failure
  (expect [scrape/extract-data
             (has-args ["<html></html>"]
                (returns nil))
           log-failure
             (has-args ["Error parsing html %s" "ABC" "2012-02-18"])]
    (process-response "ABC" "2012-02-18" {:content "<html></html>"})))

(deftest harvest-should-process-success
  (expect [fetch-symbol
             (has-args ["ABC"]
                (returns {:response-code 200}))
           util/today (returns "2012-02-18")
           process-response
             (has-args ["ABC" "2012-02-18" {:response-code 200}])]
    (harvest "ABC")))

(deftest harvest-should-log-failure
  (expect [fetch-symbol
             (has-args ["XYZ"]
                (returns {:response-code 404}))
           util/today (returns "2012-02-18")
           log-failure
             (has-args ["Error fetching html %s" "XYZ" "2012-02-18"])]
    (harvest "XYZ")))

