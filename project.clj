(defproject jepsen.chicago "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"local" ~(str (.toURI (java.io.File. "/Users/smadan/lein_repo")))}
  :dependencies [[org.clojure/clojure "1.7.0"]
                [jepsen "0.1.1-SNAPSHOT"]
                [com.xjeffrose/chicago "0.3.1-SNAPSHOT"]])
