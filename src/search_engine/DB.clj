(ns search-engine.DB
  (:require [search-engine.util :as util]))


(defonce DOCS :docs)
(defonce TOKENS :tokens)

(defonce DB (atom {DOCS {}
                   TOKENS {}}))

(defn reset-DB []
  (reset! DB {DOCS {}
              TOKENS {}}))

(defn get-DB []
  @DB)

(defn dump [& filename]
  (let [file-prefix (java.util.UUID/randomUUID)
        filename    (or filename
                        (str file-prefix ".edn"))]
    (spit filename (get-DB))
    filename))

(defn get-doc [doc-id]
  (get-in @DB [DOCS doc-id]))

(defn get-token [token-id]
  (get-in @DB [TOKENS token-id]))

(defn add-doc [doc-id tokens]
  (swap! DB assoc-in [DOCS doc-id] (set tokens)))

(defn remove-doc [doc-id]
  (swap! DB util/dissoc-in [DOCS doc-id]))

(defn remove-token [token-id]
  (swap! DB util/dissoc-in [TOKENS token-id]))

(defn add-token [token-id doc-id]
  (let [docs (get-in @DB [TOKENS token-id])]
    (swap! DB assoc-in [TOKENS token-id] (set (conj docs doc-id)))))

(defn remove-doc-in-token
  "remove doc-id in token-id"
  [token-id doc-id]
  (println (str "Removing " doc-id " in " token-id))
  (if-not (get-in @DB [TOKENS token-id])
    (util/error "Unknown token-id :" token-id)
    (let [docs (get-in @DB [TOKENS token-id])
          docs (disj docs doc-id)]
      (if (empty? docs)
        (remove-token token-id)
        (swap! DB assoc-in [TOKENS token-id] docs)))))

(comment
  (do
    (add-doc 1 ["soup" "tomato" "cream" "salt"])
    (add-doc 2 ["cake" "sugar" "eggs" "flour" "sugar" "cocoa" "cream" "butter"])
    (add-doc 1 ["bread" "butter" "salt"])
    (add-doc 3 ["soup" "fish" "potato" "salt" "pepper"]))

    ;(add-token "soup" 1)
    ;(add-token "tomato" 1)
    ;(add-token "cream" 1)
    ;(add-token "cream" 2)
    ;(add-token "cream" 3)
    ;(add-token "salt" 1)
    ;(add-token "eggs" 2))

  (remove-doc 1)
  (remove-doc-in-token "cream" 3)
  :end)
