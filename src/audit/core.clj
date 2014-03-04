(ns audit.core)

(defn regex [reg]
  (fn [url] 
    (if (re-find reg (str url))
      true false)))

;(planner {:url [string? #"\d+"]})

