(ns guis.re-frame.gui04
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [guis.common.gui04 :as gc04]))

(def ms-per-sec 1000)
(def max-duration 100)

(def db-key ::gui04)

;; Events
(rf/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    {:db (let [{:keys [duration-seconds]} (db-key db)
               t (js/Date.)]
           (assoc db db-key
                     {:duration-seconds (or duration-seconds (/ max-duration 2))
                      :start-time       t
                      :current-time     t
                      :dt-seconds       0}))
     :fx [[:dispatch [::start-timer]]]}))

(rf/reg-event-fx
  ::reset-state
  (fn [{db :db} _]
    {:db (let [{:keys [duration-seconds]} (db-key db)
               t (js/Date.)]
           (assoc db db-key
                     {:duration-seconds (or duration-seconds (/ max-duration 2))
                      :start-time       t
                      :current-time     t
                      :dt-seconds       0}))}))

(rf/reg-event-fx
  ::start-timer
  (fn [{db :db} _]
    {:db (assoc-in db [db-key :timer] (js/setInterval #(rf/dispatch [::update-dt]) 100))}))

(rf/reg-event-fx
  ::update-dt
  (fn [{db :db} _]
    {:db (let [{:keys [start-time duration-seconds]} (db-key db)
               max-time (+ (.getTime start-time) (* ms-per-sec duration-seconds))
               current-time (js/Date. (min max-time (.getTime (js/Date.))))
               dt-seconds (/ (- current-time start-time) ms-per-sec)]
           (-> db
               (assoc-in [db-key :current-time] current-time)
               (assoc-in [db-key :dt-seconds] dt-seconds)))}))

(rf/reg-event-fx
  ::set-duration-seconds
  (fn [{db :db} [_ duration-seconds]]
    {:db (assoc-in db [db-key :duration-seconds] duration-seconds)}))

;; Subscriptions
(rf/reg-sub ::dt-seconds (fn [db] (-> db db-key :dt-seconds)))
(rf/reg-sub ::duration-seconds (fn [db] (-> db db-key :duration-seconds)))
(rf/reg-sub ::start-time (fn [db] (-> db db-key :start-time)))
(rf/reg-sub ::timer (fn [db] (-> db db-key :timer)))

(defn main []
  (rf/dispatch [::initialize])
  (fn []
    (let [dt-seconds @(rf/subscribe [::dt-seconds])
          duration-seconds @(rf/subscribe [::duration-seconds])]
      [:div
       [:h2 "Task 4: Timer"]
       [:span
        [:div.input-group-prepend
         [:span {:style {:width :120px}} "Progress:"]
         [:progress {:style {:width :100%}
                     :value dt-seconds
                     :max   duration-seconds}]]
        [:div.input-group-prepend
         [:span
          [:label {:style {:width :120px}} "Elapsed Time:"]
          [:label (str dt-seconds "/" duration-seconds "s")]]]
        [:div.input-group-prepend
         [:span {:style {:width :120px}} "Duration: "]
         [:input {:style     {:width :100%}
                  :type      "range"
                  :min       0
                  :value     duration-seconds
                  :max       max-duration
                  :on-change (fn [e]
                               (let [v (.-value (.-target e))]
                                 (rf/dispatch [::set-duration-seconds v])))}]]
        [:div.input-group-prepend
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_] (rf/dispatch [::reset-state]))}
          "Reset"]]]
       gc04/about])))
