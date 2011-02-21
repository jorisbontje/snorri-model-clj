(ns snorri-model.middleware
  "Ring middleware.")

(defn wrap-if
  "Conditional wrapper based on predicate."
  [handler pred wrapper & args]
  (if pred
    (apply wrapper handler args)
    handler))
