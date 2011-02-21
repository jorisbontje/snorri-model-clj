(ns snorri-model.process
  "Process the data, calculate derivative values.")

(defmacro check-numbers
  "Check if every argument is a number. Evaluate body or otherwise return NA."
  [args body]
  `(if (every? number? ~args)
    ~body
    "NA"))

(defn filter-nils
  "Remove nils from the list."
  [l]
  (filter #(number? %) l))

(defn filter-outliers
  "Remove outliers from the list."
  [lower upper l]
  (filter #(< lower % upper) l))

(defn average
  "Calculate the average of the given numbers."
  [l]
  (if-not (empty? l)
    (let [not-nils (filter-nils l)
          sum (apply + not-nils)
          n (count not-nils)]
      (.divide (bigdec sum) (bigdec n) java.math.BigDecimal/ROUND_HALF_UP))))

(defn to-money
  "Covert the string to a Money representation"
  [s]
  (try
    (bigdec s)
    (catch IllegalArgumentException _ nil)))

(defn round
  "Round the number to 2 digits"
  [n]
  (if (nil? n)
    "NA"
    (try
      (Double/parseDouble (format "%.2f" n))
      (catch IllegalArgumentException _ "XX"))))

;; Danger, magical values
(def pe-min 7)
(def pe-max 32)

(defn calc-avg-pe
  "Calculate the average P/E over those values within the allowed range."
  [l]
  (round (average (filter-outliers pe-min pe-max (filter-nils l)))))

(defn calc-sum-es
  "Calculate the sum of the (quarterly) earning surprise values."
  [l]
  (check-numbers l
    (round (apply + l))))

(defn calc-safe-eg
  "Tune the Earnings Growth rate a bit down."
  [eg]
  (check-numbers [eg]
    (round (if (< eg 10)
      (dec eg)
      (- eg (quot eg 4))))))

(defn calc-exp
  "Calculate expected growth in 5y based on PE, ES and EG."
  [pe es eg]
  (check-numbers [pe es eg]
    (round (* es pe (Math/pow (inc (/ eg 100)) 5)))))

(defn calc-gain
  "Calculate the yearly gain based on last close and expected growth (which is
  over 5y)."
  [close exp]
  (check-numbers [close exp]
    (if (every? pos? [close exp])
      (round (* 100 (dec (Math/pow (/ exp close) 0.2))))
      "NA")))

;; Danger, magical values
(def gain-buy 20)
(def gain-sell 8)

(defn give-advise
  "Advise to BUY, SELL, or HOLD based on gain."
  [gain]
  (check-numbers [gain]
    (cond
      (>= gain gain-buy) "BUY"
      (<= gain gain-sell) "SELL"
      :else "HOLD")))

(defn enrich-data
  "Enrich stock data with analysis and trading advise."
  [{:keys [close pe es eg] :as data}]
  (let [avg-pe (calc-avg-pe (map to-money pe))
        sum-es (calc-sum-es (map to-money es))
        safe-eg (calc-safe-eg (to-money eg))
        exp (calc-exp avg-pe sum-es safe-eg)
        gain (calc-gain (to-money close) exp)
        advise (give-advise gain)]
    (assoc data :avg-pe avg-pe :sum-es sum-es :safe-eg safe-eg :exp exp :gain gain :advise advise)))
