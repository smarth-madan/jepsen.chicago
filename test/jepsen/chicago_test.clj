(ns jepsen.chicago-test
  (:require [clojure.test :refer :all]
            [jepsen.core :as jepsen]
            [jepsen.chicago :as chicago]))

(deftest chicago-test
  (is (:valid? (:results (jepsen/run! (chicago/chicago-test "v2"))))))
