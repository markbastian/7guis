(ns guis.gui01.views
  (:require [re-frame.core :as rf]))

(defn main []
  (let [clicks (rf/subscribe [::clicks])]
    [:div
     [:h2 "Task 1: Counter"]
     [:button.btn.btn-primary
      {:type     "button"
       :on-click (fn [_] (rf/dispatch [::click]))}
      "Count"
      [:span.badge.badge-light (or @clicks 0)]]
     [:h5 "About"]
     [:p "It's a counter. Click the button and the count on the badge goes up."]]))

;; Events
(rf/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    {:db (assoc db :clicks 0)}))

(rf/reg-event-fx
  ::click
  (fn [{db :db} _]
    {:db (update db :clicks (fnil inc 0))}))

;; Subscriptions
(rf/reg-sub ::clicks (fn [db] (:clicks db)))

