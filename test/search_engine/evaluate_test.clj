(ns search-engine.evaluate-test
  (:require [clojure.test :refer :all]
            [search-engine.query :refer :all]
            [search-engine.core :as core]))

;; Loading test data
(defn load-test-data []

  (core/add-doc 1 ["soup" "tomato" "cream" "salt"])
  (core/add-doc 2 ["cake" "sugar" "eggs" "flour" "sugar" "cocoa" "cream" "butter"])
  (core/add-doc 1 ["bread" "butter" "salt"])
  (core/add-doc 3 ["soup" "fish" "potato" "salt" "pepper"]))


(deftest ^:query query-validation-test
  (load-test-data)
  (testing "Valid inputs"
    (is (= #{1 2}
           (evaluate "butter")))
    (is (= #{2}
           (evaluate "sugar")))
    (is (= #{3}
           (evaluate "soup")))
    (is (= #{1 3}
           (evaluate "(butter | potato) & salt")))
    (is (= #{1 3 2}
           (evaluate "((butter | potato) & salt) | cream"))))


  (testing "Invalid inputs"
    (is (= #{1 2}
           (evaluate "butter")))))



