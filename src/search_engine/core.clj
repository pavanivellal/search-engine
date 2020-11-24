(ns search-engine.core
  (:gen-class)
  (:require
    [search-engine.util    :as util]
    [search-engine.insert  :as insert]
    [search-engine.query   :as query]
    [clojure.string        :as str]))

(defn add-doc [doc-id tokens]
  (util/status-> {:doc-id doc-id
                  :tokens tokens}
                 insert/token-validation
                 insert/doc-id-validation
                 insert/add-record-to-DB))

(defn evaluate-doc-str [insert-doc-str]
  (let [elements                 (-> (str/trim insert-doc-str)
                                     (str/split #" "))
        doc-id                   (read-string (second elements))
        tokens                   (-> (vec elements)
                                     (subvec 2))
        {:keys [valid-tokens
                invalid-tokens] :as resp} (add-doc doc-id tokens)]
    (println resp)
    (if-not (empty? valid-tokens)
      (println "index ok " doc-id)
      (println "index error invalid tokens " invalid-tokens))))

(defn evaluate-query-str [query-str]
  (let [query (-> (str/replace query-str #"query" "")
                  str/trim)
        result (query/evaluate query)]
    (if (set? result)
      (println "query result " (str/join " " result))
      (println "query result error : Not valid input"))))

(defn -main []
  (let [input (read-line)]
    (cond

      (str/includes? input "index") (do
                                      (evaluate-doc-str input)
                                      (recur))

      (str/includes? input "query") (do
                                      (evaluate-query-str input)
                                      (recur))


      (str/includes? input "DB")   (do
                                     (println "DB Contents dumped to : " (search-engine.DB/dump))
                                     (recur))

      (str/includes? input "exit")  (println "Thank you! Good Bye")

      :else                         (do
                                      (println "Bad input! Try again")
                                      (recur)))))


(comment
  :end)
