(ns owlet-status-ui.app
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]))

(def domains ["google.com" "yahoo.com" "http://www.zappos.com/" "cnet.com" "facebook.com"])

(def statuses (atom []))

; (enable-console-print!)

; (add-watch statuses :logger #(-> %4 clj->js js/console.log))

(defn foofunction [domain]
  (GET (str "http://localhost:3000/api/ping/" domain)
    {:keywords? true
     :handler (fn [res]
      (.log js/console res)
      (swap! statuses conj (str domain " : " (get res "time"))))}))

(let []
  (doseq [d domains]
    (foofunction d)))

(defn listfoo []
  (let []
    [:ul 
    (for [d @statuses]
      ^{:key d}
      [:li 
        [:span d]])]))

(defn main []
  [:div "Parent component"
   [listfoo]])

(defn init []
  (reagent/render-component [main]
                            (.getElementById js/document "container")))
