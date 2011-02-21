(ns snorri-model.scrape
  "Scrape HTML."
  (:require [appengine-magic.services.url-fetch :as url-fetch]
            [clojure.string :as string]
            [snorri-model.process :as process]))

(def scrape-base-url "http://moneycentral.msn.com/investor/research/sreport.asp?Symbol=%s&QD=1&AIE=1&FRH=1&FRK=1&Type=Equity")

(defn get-scrape-url [symbol]
  (format scrape-base-url symbol))

(defn filter-span
  "Remove <span> tag from the html."
  [html]
  (if-let [match (re-matches #"<span.*?>(.*?)</span>" html)]
    (get match 1)
    html))

(defn filter-percent
  "Remove % from the html."
  [html]
  (string/replace html "%" ""))

(defn filter-plus
  "Remove + from the html."
  [html]
  (string/replace html "+" ""))

(defn match-to-money
  "Convert a regex match to money"
  [m]
  (map #(-> %
         (filter-percent)
         (filter-plus)
         (filter-span)) (rest m)))

(defn extract-close
  "Extract the closing price from the html"
  [html]
  (if-let [match (re-find #"<tr><td>Previous Close</td><td.*?>(.*?)</td></tr>" html)]
    (first (match-to-money match))))

(defn extract-10yPE
  "Extract the P/E of the last 10 yearn from the html"
  [html]
  (if-let [block-match (re-find #"<table><thead><tr>.*?>Avg P/E</th>.*?<tbody>(.*?)</tbody></table>" html)]
    (let [pe-lines (re-seq #"<tr><td>.*?</td><td.*?>(.*?)</td>" (get block-match 1))]
      (map #(first (match-to-money %)) pe-lines))))

(defn extract-1yES
  "Extract the actual Earnings Surprise of the last 4 quarters from the html"
  [html]
  (if-let [match (re-find #"<tr><td>Actual</td><td.*?>.*?</td><td.*?>(.*?)</td><td.*?>(.*?)</td><td.*?>(.*?)</td><td.*?>(.*?)</td></tr>" html)]
    (match-to-money match)))

(defn extract-5yEG
  "Extract the company Earnings Growth rate for the next 5 years from the html"
  [html]
  (if-let [match (re-find #"<tr><td>Company</td><td.*?>.*?</td><td.*?>.*?</td><td.*?>.*?</td><td.*?>(.*?)</td><td.*?>.*?</td></tr>" html)]
    (first (match-to-money match))))

(defn extract-data [html]
  "Extract all data from given html."
  (let [close (extract-close html)
        pe (extract-10yPE html)
        es (extract-1yES html)
        eg (extract-5yEG html)]
    (if (and close pe es eg)
      {:close close :pe pe :es es :eg eg})))
