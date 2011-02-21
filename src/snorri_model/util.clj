(ns snorri-model.util
  "Utilities, date and logging related.")

(defn format-date
  "Format the given date."
  ([date]
   (format-date "yyyy-MM-dd" date))
  ([pattern date]
   (.format (doto (java.text.SimpleDateFormat. pattern)
                  (.setTimeZone (java.util.TimeZone/getTimeZone "UTC"))) date)))

(defn today
  "Return todays date in the default format."
  []
  (format-date (java.util.Date.)))

(defn now
  "Return the current date and time."
  []
  (format-date "yyyy-MM-dd HH:mm:ss.SSS" (java.util.Date.)))

(defn log
  "Log to stderr."
  [msg & vals]
  (let [line (apply format msg vals)]
    (binding [*out* *err*]
      (println line))))
