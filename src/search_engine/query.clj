(ns search-engine.query
  (:require [clojure.set           :as set]
            [search-engine.DB      :as DB]))

(def precedence '{& 0
                  | 1})

(def ops {'& set/intersection
          '| set/union})

(defn ops-order
  "decides the order of operation according to precedence"
  [[A x B y C & more]]
  (let [ret (if (<=  (precedence x)
                     (precedence y))
              (list (list A x B) y C)
              (list A x (list B y C)))]
    (if more
      (recur (concat ret more))
      ret)))

(defn add-parens
  "Depth First Search (Post Order Traversal) to find all nested equations and add parameters to expression"
  [s]
  (clojure.walk/postwalk
    #(if (seq? %)
       (let [c (count %)]
         (cond (even? c) (throw (Exception. "Must be an odd number of forms"))
               (= c 1) (first %)
               (= c 3) %
               (>= c 5) (ops-order %)))
       %)
    s))

(defn parse-expression
  "Parse a query string into a list of tokens, operators and lists"
  [s]
  (-> (format "'(%s)" s)
      (.replaceAll , "([&|])" " $1 ")
      load-string
      add-parens))

(def evaluate-query-helper
  "evaluate query based on operations"
  (partial clojure.walk/postwalk
        #(if (seq? %)
           (do
             (println "cond 1 -" %)
             (let [[a-set o b-set] %]
               ((ops o) a-set b-set)))
           (do
             (println "cond 2 - " %)
             (or (DB/get-token (name %))
                 %)))))

(defn evaluate [s]
  "Parse and evaluate query"
  (evaluate-query-helper (parse-expression s)))

(comment

  (DB/reset-DB)
  (search-engine.core/add-doc 1 ["soup" "tomato" "cream" "salt"])
  (search-engine.core/add-doc 2 ["cake" "sugar" "eggs" "flour" "sugar" "cocoa" "cream" "butter"])
  (search-engine.core/add-doc 1 ["bread" "butter" "salt"])
  (search-engine.core/add-doc 3 ["soup" "fish" "potato" "salt" "pepper"])

  (= #{1 2}
    (evaluate "butter"))
  (= #{2}
    (evaluate "sugar"))
  (= #{3}
    (evaluate "soup"))
  (= #{1 3}
     (evaluate "(butter | potato) & salt"))
  (= #{1 3 2}
    (evaluate "((butter | potato) & salt) | cream"))

  :end)
