(ns snorri-model.util)

(defn format-date
  ([date]
   (format-date "yyyy-MM-dd" date))
  ([pattern date]
   (.format (doto (java.text.SimpleDateFormat. pattern)
                  (.setTimeZone (java.util.TimeZone/getTimeZone "UTC"))) date)))
