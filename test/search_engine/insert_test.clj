(ns search-engine.insert-test
  (:require [clojure.test           :refer :all]
            [search-engine.insert   :refer :all]
            [search-engine.core     :as core]
            [search-engine.DB       :as DB]))

(deftest ^:insert insert-test
  ;; Reset DB every time
  (DB/reset-DB)

  (testing "Insert doc-id 1"
    (is (= {:doc-id 1,
            :tokens ["soup" "tomato" "cream" "salt"],
            :valid-tokens '("soup" "tomato" "cream" "salt"),
            :invalid-tokens nil}
          (core/add-doc 1 ["soup" "tomato" "cream" "salt"])))
    (is (= {:docs {1 #{"cream" "soup" "salt" "tomato"}},
            :tokens {"soup" #{1}, "tomato" #{1}, "cream" #{1}, "salt" #{1}}}
           (DB/get-DB))))

  (testing "Insert doc-id 2"
    (= {:doc-id 2,
        :tokens ["cake" "sugar" "eggs" "flour" "sugar" "cocoa" "cream" "butter"],
        :valid-tokens '("cake" "sugar" "eggs" "flour" "sugar" "cocoa" "cream" "butter"),
        :invalid-tokens nil}
       (core/add-doc 2 ["cake" "sugar" "eggs" "flour" "sugar" "cocoa" "cream" "butter"]))
    (is (= {:docs {1 #{"cream" "soup" "salt" "tomato"},
                   2 #{"eggs" "sugar" "cocoa" "cream" "flour" "cake" "butter"}},
            :tokens {"eggs" #{2},
                     "sugar" #{2},
                     "cocoa" #{2},
                     "cream" #{1 2},
                     "flour" #{2},
                     "soup" #{1},
                     "cake" #{2},
                     "butter" #{2},
                     "salt" #{1},
                     "tomato" #{1}}}
           (DB/get-DB))))

  (testing "Update doc-id 1"
    (is (= {:doc-id 1, :tokens ["bread" "butter" "salt"],
            :valid-tokens '("bread" "butter" "salt"),
            :invalid-tokens nil}

           (core/add-doc 1 ["bread" "butter" "salt"])))
    (is (= {:docs {1 #{"bread" "butter" "salt"},
                   2 #{"eggs" "sugar" "cocoa" "cream" "flour" "cake" "butter"}},
            :tokens {"bread" #{1},
                     "eggs" #{2},
                     "sugar" #{2},
                     "cocoa" #{2},
                     "cream" #{2},
                     "flour" #{2},
                     "cake" #{2},
                     "butter" #{1 2},
                     "salt" #{1}}}
         (DB/get-DB))))


  (testing "Update doc-id 3"
    (is (= {:doc-id 3,
            :tokens ["soup" "fish" "potato" "salt" "pepper"],
            :valid-tokens '("soup" "fish" "potato" "salt" "pepper"),
            :invalid-tokens nil}
          (core/add-doc 3 ["soup" "fish" "potato" "salt" "pepper"])))
    (is (= {:docs {1 #{"bread" "butter" "salt"},
                   2 #{"eggs" "sugar" "cocoa" "cream" "flour" "cake" "butter"},
                   3 #{"pepper" "soup" "salt" "fish" "potato"}},
            :tokens {"bread" #{1},
                     "eggs" #{2},
                     "sugar" #{2},
                     "cocoa" #{2},
                     "cream" #{2},
                     "pepper" #{3},
                     "flour" #{2},
                     "soup" #{3},
                     "cake" #{2},
                     "butter" #{1 2},
                     "salt" #{1 3},
                     "fish" #{3},
                     "potato" #{3}}}
           (DB/get-DB))))



  (testing "Invalid token id"
    (is (= {:doc-id 3, :tokens ["soup" "carrot@123cake"],
            :valid-tokens '("soup"),
            :invalid-tokens '("carrot@123cake")}
           (core/add-doc 3 ["soup" "carrot@123cake"]))))

  (testing "Invalid doc id"
    (is (= {:doc-id "1",
            :tokens ["soup" "carrot@123cake"],
            :valid-tokens '("soup"),
            :invalid-tokens '("carrot@123cake"),
            :status :error,
            :message "invalid doc-id"}
           (core/add-doc "1" ["soup" "carrot@123cake"])))))

