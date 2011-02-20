(ns snorri-model.util)

(defn format-date
  ([date]
   (format-date "yyyy-MM-dd" date))
  ([pattern date]
   (.format (doto (java.text.SimpleDateFormat. pattern)
                  (.setTimeZone (java.util.TimeZone/getTimeZone "UTC"))) date)))

(defn today []
  (format-date (java.util.Date.)))

(defn now []
  (format-date "yyyy-MM-dd HH:mm:ss.SSS" (java.util.Date.)))

(defn log [msg & vals]
  (let [line (apply format msg vals)]
    (binding [*out* *err*]
      (println line))))
