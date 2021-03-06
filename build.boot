(set-env!
 :source-paths    #{"src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "1.7.228-1"  :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.0"      :scope "test"]
                 [adzerk/boot-reload        "0.4.5"      :scope "test"]
                 [pandeiro/boot-http        "0.7.2"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.12"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript "1.7.228"]
                 [reagent "0.5.0"]
                 [cljs-ajax "0.5.4"]
                 [cljsjs/d3 "3.5.16-0"]
                 [cljsjs/plottable "1.12.0-0"]
                 [org.clojure/core.async "0.2.374"]
                 [cljs-http "0.1.40"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask build []
  (comp (speak)
        (sift :add-jar {'cljsjs/plottable #"^cljsjs/plottable/common/plottable.css$"})
        (cljs)))

(deftask run []
  (comp (serve :port 5000)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none :source-map true}
                 reload {:on-jsload 'owlet-status-ui.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (sift :add-jar {'cljsjs/plottable #"^cljsjs/plottable/common/plottable.css$"})
        (development)
        (run)))


