(ns audit.core)

(defn regex
  "Takes a regular express and returns a funtion.
   the returning function takes input, turns it into
   a string and runs re-find with the regex and the input."
  [reg]
  (fn [input] 
    (re-find reg (str input))))

(defn truthy
  "Turns truthy into true and false and nil into false"
  [value]
  (if value true false))

(defn audit-helper
  "Takes two maps, the value map and the audit map.
   checks to see if valid-audit key is truthy, if not it
   doesn't run anything.  If so, it checks every fn in the
   vector and applies the true or false value to the map,
   under the key valid-audit."
  [value-map [k v]]
  (if-not (:valid-audit value-map)
    value-map
    (let [input (get value-map k)
          result (every? #(truthy (% input)) v)]
      (if result
        value-map
        (assoc value-map
          :valid-audit result
          :failure-reason (str k " with input " input))))))

(defn audit
  "Takes two maps audit-map and value-map.  First check to see
   if they have the same keys.  If not, it throws an exception.
   If so, the valid-audit key is set to true in the value-map, and
   the audit-map is reduced using audit-helper. The value-map is
   the initial value for the reduce operation."
  [audit-map value-map]
  (if-not (= (sort (keys audit-map))
             (sort (keys value-map)))
    (throw (Exception. "Maps must have the same keys"))
    (let [value-map (assoc value-map :valid-audit true)]
      (reduce audit-helper value-map audit-map))))
