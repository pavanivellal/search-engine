(ns search-engine.util
  (:require [clojure.string :as str]))

(defn dissoc-in
  [m [k & ks]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn error [& message]
  (let [msg (str/join " " message)]
    (println "ERROR : " msg)
    {:error msg}))

(defn alphanumeric? [val]
  (re-matches #"^[a-zA-Z0-9]+$" val))

(defmacro status->
  "When the key :status is :continue :ok or nil, threads it into the first form (via ->),
  and when the key :status of that result is :continue or nil, through the next etc"
  [expr & forms]
  (let [g (gensym)
        pstep (fn [step] `(if (or (contains? #{:continue :ok} (:status ~g))
                                  (nil? (:status ~g)))
                            (merge ~g (-> ~g ~step))
                            ~g))]
    `(let [~g ~expr
           ~@(interleave (repeat g) (map pstep forms))]
       ~g)))
