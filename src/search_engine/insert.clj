(ns search-engine.insert
  (:require
    [search-engine.validations :refer :all]
    [clojure.set          :as set]
    [search-engine.DB     :as DB]))

(defn doc-id-validation [{:keys [doc-id] :as body}]
  (when-not (valid-doc-id? doc-id)
    (invalid-index-error doc-id)
    {:status :error
     :message "invalid doc-id"}))


(defn token-validation  [{:keys [doc-id tokens] :as body}]
  (let [valid-tokens    (filter valid-token? tokens)
        invalid-tokens  (-> (set/difference (set tokens) (set valid-tokens))
                            seq)
        res             {:valid-tokens valid-tokens
                         :invalid-tokens (seq invalid-tokens)}]
    (invalid-token-error doc-id invalid-tokens)
    (if (empty? valid-tokens)
      (merge res {:status :error
                  :message "No valid tokens"})
      res)))

(defn add-record-to-DB [{:keys [doc-id valid-tokens]}]
  (when (DB/get-doc doc-id)
    (println "Updating : " (map #(DB/remove-doc-in-token % doc-id) (get-in @DB/DB [DB/DOCS doc-id]))))
  (println "Saving : "   (DB/add-doc doc-id valid-tokens))
  (println "Saving : "   (map #(DB/add-token % doc-id) valid-tokens)))
