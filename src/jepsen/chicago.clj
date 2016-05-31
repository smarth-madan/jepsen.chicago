(ns jepsen.chicago
  (:import [com.xjeffrose.chicago.client ChicagoClient])
  (:require [clojure.tools.logging :refer :all]
            [jepsen.tests :as tests]
            [jepsen.db :as db]
            [jepsen.control :as c]
            [jepsen.client :as client]
            [jepsen.os.debian :as debian]
            [jepsen.control.util :as cu]
            [jepsen.generator :as gen]
            [jepsen.checker :as checker]
            [jepsen.util :refer [timeout]]
            [knossos.model      :as model]))


(defn db
  "A chicago client"
  [version]
  (reify db/DB
    (setup! [_ test node]
      (info node "installing Chicago" version)
      (c/su
        ;(info node (cu/exists? "/home/smadan/chicago/chicago"))
        ()))

    (teardown! [_ test node]
      (info node "tearing down Chicago"))
      ;(c/su
      ;  (cu/grepkill! "chicago")))

    db/LogFiles
    (log-files [_ test node]
      ["/home/smadan/chicago/chicago.out"])))

(defn client
  "A client for a single compare-and-set register"
  [conn]
  (reify client/Client
    (setup! [_ test node]
        (let [conn (ChicagoClient. "10.24.25.188:2181,10.24.25.189:2181,10.25.145.56:2181,10.24.33.123:2181" 3)]
          (client conn)))

    (invoke! [this test op]
      (timeout 5000 (assoc op :type :info, :error :timeout)
               (case (:f op)
                 :read (assoc op :type :ok, :value (String. (.get (. conn read (bytes (byte-array (map (comp byte int) "key0")))))))
                 :write (assoc op :type :ok, :value (. conn write (bytes (byte-array (map (comp byte int) (str "key" "12")))) (bytes (byte-array (map (comp byte int) (str "val" "12")))))
                        ))))

    (teardown! [_ test])))

(defn r   [_ _] {:type :invoke, :f :read, :value nil})
(defn w   [_ _] {:type :invoke, :f :write, :value (rand-int 500)})


(defn chicago-test
  [version]
  (assoc tests/noop-test
         :db (db version)
         :client (client nil)
         :generator (->> (gen/mix [r w])
            (gen/stagger 1)
            (gen/clients)
            (gen/time-limit 5))))
