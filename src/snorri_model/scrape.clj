(ns snorri-model.scrape
  (:require [appengine-magic.services.url-fetch :as url-fetch]))

(def scrape-url "http://moneycentral.msn.com/investor/research/sreport.asp?Symbol=%s&QD=1&AIE=1&FRH=1&FRK=1&Type=Equity")

(defn get-scrape-url [symbol]
  (format scrape-url symbol))

(defn to-double
  "Convert the string to a double, returns nil if not a valid number."
  [s]
  (try
    (Double/parseDouble s)
    (catch NumberFormatException _ nil)))

(defn extract-close [html]
  (if-let [match (re-matches #"<tr><td>Previous Close</td><td.*?>(.*?)</td></tr>" html)]
    (to-double (get match 1))))

(defn filter-span
  "Remove <span> tag from the html."
  [html]
  (if-let [match (re-matches #"<span.*?>(.*?)</span>" html)]
    (get match 1)
    html))

(defn filter-outliers
  "Remove outliers from the list."
  [lower upper l]
  (filter #(< lower % upper) l))

(defn filter-nils
  "Remove nil values from the list."
  [l]
  (filter #(not (nil? %)) l))

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

(defn extract-avg10yPE [html]
  (if-let [block-match (re-matches #"<table><thead><tr>.*?>Avg P/E</th>.*?<tbody>(.*?)</tbody></table>" html)]
    (let [pe-lines (re-seq #"<tr><td>.*?</td><td.*?>(.*?)</td>" (get block-match 1))
          pe-list (map #(to-double (filter-span (get % 1))) pe-lines)]
      (round (average (filter-outliers pe-min pe-max (filter-nils pe-list)))))))


