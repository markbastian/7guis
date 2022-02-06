(ns guis.re-frame.gui01
  (:require [re-frame.core :as rf]
            [guis.common.gui01 :as gc1]))

(def db-key ::gui01)

(defn main []
  (rf/dispatch [::initialize])
  (fn []
    (let [clicks (rf/subscribe [::clicks])]
      [:div
       [:h2 "Task 1: Counter"]
       [:button.btn.btn-primary
        {:type     "button"
         :on-click (fn [_] (rf/dispatch [::click]))}
        "Count"
        [:span.badge.badge-light (or @clicks 0)]]
       gc1/about])))

;; Events
(rf/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    {:db (assoc-in db [db-key :clicks] 0)}))

(rf/reg-event-fx
  ::click
  (fn [{db :db} _]
    {:db (update-in db [db-key :clicks] (fnil inc 0))}))

;; Subscriptions
(rf/reg-sub ::clicks (fn [db] (-> db db-key :clicks)))

