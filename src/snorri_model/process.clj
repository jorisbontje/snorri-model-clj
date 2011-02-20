(ns snorri-model.process)

(defmacro check-numbers
  "Check if every argument a number."
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
    (catch NumberFormatException _ nil)))

(defn round
  "Round the number to 2 digits"
  [n]
  (Double/parseDouble (format "%.2f" n)))

(def pe-min 7)
(def pe-max 32)

(defn calc-avg-pe [l]
  (round (average (filter-outliers pe-min pe-max (filter-nils l)))))

(defn calc-sum-es [l]
  (check-numbers l
    (round (apply + l))))

(defn calc-safe-eg [eg]
  (check-numbers [eg]
    (round (if (< eg 10)
      (dec eg)
      (- eg (quot eg 4))))))

(defn calc-exp [pe es eg]
  (check-numbers [pe es eg]
    (round (* es pe (Math/pow (inc (/ eg 100)) 5)))))

(defn calc-gain [close exp]
  (check-numbers [close exp]
    (if (pos? close)
      (round (* 100 (dec (Math/pow (/ exp close) 0.2))))
      0.0)))

(defn give-advise [gain]
  (check-numbers [gain]
    (cond
      (>= gain 20) "BUY"
      (<= gain 8) "SELL"
      :else "HOLD")))

(defn enrich-data [{:keys [close pe es eg] :as data}]
  (let [avg-pe (calc-avg-pe (map to-money pe))
        sum-es (calc-sum-es (map to-money es))
        safe-eg (calc-safe-eg (to-money eg))
        exp (calc-exp avg-pe sum-es safe-eg)
        gain (calc-gain (to-money close) exp)
        advise (give-advise gain)]
    (assoc data :avg-pe avg-pe :sum-es sum-es :safe-eg safe-eg :exp exp :gain gain :advise advise)))
