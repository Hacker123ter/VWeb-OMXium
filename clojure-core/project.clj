(defproject clojure-core "0.1.0-ALPHA"
  :description "Clojure‑модуль для парсинга и логики маршрутов VWeb Omxium"
  :url "https://github.com/Hacker123ter/VWeb-Omxium"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :repl-options {:init-ns clojure-core.core}
  :source-paths ["src/clojure_core"]
  :main clojure-core.core
  :aot [clojure-core.core]
  :test-paths ["test/clojure_core"])
