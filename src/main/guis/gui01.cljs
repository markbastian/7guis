(ns guis.gui01
  (:require [reagent.core :as r]))

(defn render []
  (let [count (r/atom 0)]
    (fn []
      [:div
       [:h2 "Task 1: Counter"]
       [:button.btn.btn-primary
        {:type     "button"
         :on-click (fn [_] (swap! count inc))}
        "Count"
        [:span.badge.badge-light @count]]])))