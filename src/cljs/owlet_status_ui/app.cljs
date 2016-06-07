(ns owlet-status-ui.app
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [cljsjs.plottable]
            [cljs.core.async :refer [<! >! put! chan]]
            [cljs-http.client :as http]))

(enable-console-print!)

(declare ping!)

(def statuses (atom []))
(def articles (atom []))

;(def domains (take 50 (cycle ["google.com" "www.zappos.com" "cnet.com" "facebook.com" "galvanize.com"
;                              "twitter.com" "facebook.com" "octoblu.com" "levelsbeyond.com" "github.com" "digitalocean.com"
;                              "clojure.org" "clojuredocs.org"])))

(def domains
  ["owlet-cms.apps.aterial.org" "api.contentful.com" "codefordenver.auth0.com" "cdn.contentful.com"])

;(add-watch articles :logger #(-> %4 clj->js js/console.log))

;; TODO:
;; ----------------------
;; - add core.async / http-cljs

(defn jsrand [& [seed]]
      (js/Math.floor (* (js/Math.random) (or seed 10))))

(def data
  [
   {:fill "#FFFFFF" :x (jsrand 0) :x2 10 :y 0 :y2 (jsrand 20)}
   {:fill "#FF0000" :x (jsrand 0) :x2 10 :y 10 :y2 (jsrand 20)}
   {:fill "#0000FF" :x 13 :x2 20 :y 0 :y2 7}
   {:fill "#FFFFFF" :x 13 :x2 20 :y 7 :y2 13}
   {:fill "#FFFF00" :x 13 :x2 20 :y 13 :y2 20}
   {:fill "#FFFF00" :x 20 :x2 30 :y (jsrand 0) :y2 10}
   {:fill "#0000FF" :x 20 :x2 (jsrand 27) :y 10 :y2 13}
   ])

(defn rect [x y c]
      {:fill c :x 0 :x2 13 :y 0 :y2 7})

;; -- plottable.js setup ----------------------------------------------

(def xScale (js/Plottable.Scales.Linear.))
(def yScale (js/Plottable.Scales.Linear.))

(def plot (js/Plottable.Plots.Rectangle.))

(.addDataset plot (js/Plottable.Dataset. (clj->js data)))

;(def xAxis (js/Plottable.Axes.Category. "top"))
;(def yAxis (js/Plottable.Axes.Category. "left"))

(doto plot
      (.x #(.-x %) xScale)
      (.y #(.-y %) yScale)
      (.x2 #(.-x2 %) xScale)
      (.y2 #(.-y2 %) yScale))

(.attr plot "fill" #(.-fill %))
(.attr plot "stroke" (fn [] "#000000"))
(.attr plot "stroke-width" (fn [] 4))

;(defn hn-top-500!
;      "get 500-ish articles form hacker news"
;      []
;      (GET "https://hacker-news.firebaseio.com/v0/topstories.json"
;           {:keywords? true
;            :handler   #(reset! articles %)}))

(defn ping! [domain & [co]]
      (if co
        (let [w {:with-credentials? false}]
             (go
               (let [res (<! (http/get (str "http://localhost:3000/api/ping/" domain) w))]
                    (swap! statuses conj (str domain " : " (get-in res [:body :time]))))))
        (GET (str "http://localhost:3000/api/ping/" domain)
             {:keywords? true
              :handler   (fn [res]
                             (swap! statuses conj (str domain " : " (get res "time"))))})))

(defn list-component []
      [:ul
       (for [d @statuses]
            ^{:key d}
            [:li
             [:span d]])])

(defn main []
      (let []
           (reagent/create-class
             {:component-did-mount
              (fn []
                  (doseq [d domains]
                         (ping! d true))
                  (.renderTo plot "svg#mondrian")
                  #_(GET (str "https://hacker-news.firebaseio.com/v0/item/" (str 11789920) ".json")
                         {:keywords? true
                          :handler   (fn [res]
                                         (let [host (.-hostname (new js/URL (get res "url")))]
                                              (ping! host)))})
                  )
              :reagent-render
              (fn []
                  [:div
                   ;[:input {:type "range" :style {:width "100%"}}]
                   [:svg#mondrian]
                   [list-component]])})))

(defn init []
      ;(hn-top-500!)
      (reagent/render-component [main]
                                (.getElementById js/document "container")))