(ns snorri-model.test.process
  (:use [snorri-model.process] :reload)
  (:use [clojure.test :only [deftest is]]))

(deftest should-average-bigdecimals
  (is (= 20.16 (average [14.80M 16.90M 16.60M 18.10M 20.70M 23.40M 30.60M]))))

(deftest should-calc-safe-eg
  (is (= 8.0 (calc-safe-eg 9.0)))
  (is (= 9.0 (calc-safe-eg 11.0))))

(deftest should-calc-exp
  (is (= 243.2 (calc-exp 11.0 12.0 13.0))))

(deftest should-calc-gain
  (is (= 0.0 (calc-gain 0.0 1.0)))
  (is (= 15.05 (calc-gain 12.4 25.0))))

(deftest should-give-advise
  (is (= "BUY" (give-advise 20.0)))
  (is (= "SELL" (give-advise 8.0)))
  (is (= "HOLD" (give-advise 15.0))))

(deftest should-enrich-data
  (is (= {:symbol "BBBY" :close 15.12 :pe 11.3 :es 12.3 :eg 8.3
          :safe-eg 7.3 :advise "BUY", :gain 67.22, :exp 197.69}
         (enrich-data {:symbol "BBBY" :close 15.12 :pe 11.3 :es 12.3 :eg 8.3}))))
