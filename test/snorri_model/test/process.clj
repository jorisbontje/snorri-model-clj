(ns snorri-model.test.process
  (:use [snorri-model.process] :reload)
  (:use [clojure.test :only [deftest is]]))

(deftest should-calc-secure-EG
  (is (= 8.0 (calc-secure-EG 9.0)))
  (is (= 9.0 (calc-secure-EG 11.0))))

(deftest should-calc-exp
  (is (= 243.2 (calc-exp 11.0 12.0 13.0))))
