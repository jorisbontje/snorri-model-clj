(ns snorri-model.util)

(defn format-date
  ([date]
   (format-date "yyyyMMdd" date))
  ([pattern date]
   (.format (doto (java.text.SimpleDateFormat. pattern)
                  (.setTimeZone (java.util.TimeZone/getTimeZone "UTC"))) date)))

(defn now []
  (format-date (java.util.Date.)))
