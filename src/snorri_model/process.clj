(ns snorri-model.process)

(defn filter-nils
  "Remove nil values from the list."
  [l]
  (filter #(not (nil? %)) l))

(defn filter-outliers
  "Remove outliers from the list."
  [lower upper l]
  (filter #(< lower % upper) l))

(defn average
  "Calculate the average of the given numbers."
  [l]
  (if-not (empty? l)
    (/ (apply + l) (count l))))

(defn round
  "Round the number to 2 digits"
  [n]
  (Double/parseDouble (format "%.2f" n)))

(def pe-min 7)
(def pe-max 32)

(defn calc-avg10yPE [l]
  (round (average (filter-outliers pe-min pe-max l))))

(defn calc-secure-EG [eg]
  (if (< eg 10)
    (dec eg)
    (- eg (quot eg 4))))

(defn calc-exp [pe es eg]
  (round (* es pe (Math/pow (inc (/ eg 100)) 5))))
