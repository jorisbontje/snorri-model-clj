(ns snorri-model.test.process
  (:use [snorri-model.process] :reload)
  (:use [clojure.test :only [deftest is]]))

(deftest should-check-numbers
  (is (= 5 (check-numbers [1] 5)))
  (is (= "NA" (check-numbers ["x"] 5)))
  (is (= 5 (check-numbers [1 2] 5)))
  (is (= "NA" (check-numbers [1 "x"] 5))))

(deftest should-average-bigdecimals
  (is (= 20.16 (average [14.80M 16.90M 16.60M 18.10M 20.70M 23.40M 30.60M]))))

(deftest should-average-bigdecimals-with-nil
  (is (= 21.05 (average [nil 16.90M 16.60M 18.10M 20.70M 23.40M 30.60M]))))

(deftest should-average-bigdecimals-with-NA
  (is (= 21.05 (average ["NA" 16.90M 16.60M 18.10M 20.70M 23.40M 30.60M]))))


;; tests to discover why DEO was giving a NumberFormatException: For input string: "nu" on calling calc-avg-pe
(deftest should-to-money-DEO
  (is (= [159.60M 146.70M 235.40M 251.50M 148.40M 191.60M 145.00M 519.30M 96.90M 283.90M]
         (map to-money ["159.60" "146.70" "235.40" "251.50" "148.40" "191.60" "145.00" "519.30" "96.90" "283.90"]))))

(deftest should-filter-all-outliers-DEO
  (let [pe [159.60M 146.70M 235.40M 251.50M 148.40M 191.60M 145.00M 519.30M 96.90M 283.90M]]
    (is (= [] (filter-outliers pe-min pe-max pe)))))

(deftest should-average-empty-list-DEO
  (is (= nil (average []))))

(deftest should-round-nil-DEO
  (is (= "NA" (round nil))))

(deftest should-round-invalid-DEO
  (is (= "XX" (round "invalid"))))

;; NA because all are filtered out
(deftest should-calc-avg-pe-DEO
  (let [pe [159.60M 146.70M 235.40M 251.50M 148.40M 191.60M 145.00M 519.30M 96.90M 283.90M]]
    (is (= "NA" (calc-avg-pe pe)))))

(deftest should-calc-safe-eg
  (is (= 8.0 (calc-safe-eg 9.0)))
  (is (= 9.0 (calc-safe-eg 11.0))))

(deftest should-calc-exp
  (is (= 243.2 (calc-exp 11.0 12.0 13.0))))

(deftest should-calc-gain
  (is (= "NA" (calc-gain 0.0 1.0)))
  (is (= 15.05 (calc-gain 12.4 25.0))))

(deftest should-give-advise
  (is (= "BUY" (give-advise 20.0)))
  (is (= "SELL" (give-advise 8.0)))
  (is (= "HOLD" (give-advise 15.0))))

(deftest should-enrich-data
  (is (= {:symbol "BBBY" :close 15.12
          :pe [14.80 16.90 16.60 18.10 20.70 23.40 30.60 33.90 40.50 35.20]
          :es [0.86 0.52 0.70 0.74] :eg 8.3
          :avg-pe 20.2 :sum-es 2.82 :safe-eg 7.3 :advise "BUY" :gain 39.9 :exp 81.02}
         (enrich-data {:symbol "BBBY" :close 15.12
                       :pe [14.80 16.90 16.60 18.10 20.70 23.40 30.60 33.90 40.50 35.20]
                       :es [0.86 0.52 0.70 0.74] :eg 8.3}))))

(deftest should-enrich-data-with-NA
  (is (= {:gain "NA", :es ["1.23"], :close "10.12", :safe-eg "NA", :advise "NA",
          :avg-pe 10.0, :exp "NA", :pe ["10.0"], :eg "NA", :sum-es 1.23}
         (enrich-data {:close "10.12" :pe ["10.0"] :es ["1.23"] :eg "NA"}))))
