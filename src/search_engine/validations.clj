(ns search-engine.validations
  (:require [search-engine.util :as util]))
(defn valid-doc-id? [doc-id]
  (integer? doc-id))

(defn valid-token? [token]
  (util/alphanumeric? token))

(defn invalid-token-error [doc-id tokens]
  (when-not (empty? tokens)
    (util/error (str doc-id " - error Invalid tokens: " tokens))))

(defn invalid-index-error [doc-id]
  (util/error (str doc-id "- error Invalid doc-id: " doc-id)))
