(ns audit.core-test
  (:require [clojure.test :refer :all]
            [audit.core :refer :all]))

(deftest regex-test
  (testing "returns a function"
    (is (= (function? (regex #"\d+")) true)))
  (testing "does regex operations"
    (is (= ((regex #"\d+") "500") "500"))))

(deftest truthy-test
  (testing "turns truthy into true"
    (is (= (truthy 1) true))
    (is (= (truthy "1") true))
    (is (= (truthy :1) true))
    (is (= (truthy []) true))
    (is (= (truthy {}) true))
    (is (= (truthy #{}) true))
    (is (= (truthy '()) true)))
  (testing "turns falsy into false"
    (is (= (truthy false) false))
    (is (= (truthy nil) false))))

(deftest audit-helper-test 
  (let [value-map {:valid-audit true :happy "sad"}
        audit-map [:happy [string? (regex #"\w")]]
        audit-map2 [:happy [string? (regex #"\d")]]
        audit-map3 [:happy [string? (regex #"\w") number?]]
        ]
    (testing "keeps vaild-audit to true"
      (is (= (:valid-audit (audit-helper value-map audit-map))
             true)))
    (testing "changes valid-audit to false"
      (is (= (:valid-audit (audit-helper value-map audit-map2))
             false)))
    (testing "multiple functions are ran"
      (is (= (:valid-audit (audit-helper value-map audit-map3))
             false)))
    (testing "should tell the reason for failure"
      (is (= (:failure-reason (audit-helper value-map audit-map3))
             ":happy with input sad")))))

(deftest audit-test
  (let [value-map {:url "http://www.google.com"
                   :title "the number 1"
                   :time 400}
        audit-map {:url [string? (regex #"http://www.")]
                   :title [string? (regex #"\d")]
                   :time [number? #(> % 300)]}
        good-audit (audit audit-map value-map)
        bad-audit (audit audit-map (assoc value-map :time 200))]
    (testing "an expection on key amounts"
      (is (= (try
               (audit {:happy "you"} {})
               (catch Exception e :exception-thrown))
             :exception-thrown)))
    (testing "good input"
      (is (= (map? good-audit) true))
      (is (= (:valid-audit good-audit) true))
      (is (= (:failure-reason good-audit) nil)))
    (testing "bad input"
      (is (= (map? bad-audit) true))
      (is (= (:valid-audit bad-audit) false))
      (is (= (:failure-reason bad-audit) ":time with input 200")))))
