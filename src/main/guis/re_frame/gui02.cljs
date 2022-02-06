(ns guis.re-frame.gui02
  (:require [re-frame.core :as rf]
            [guis.common.gui02 :as gc2]))

(def db-key ::gui02)

(defn render-temp [lbl scale]
  (let [temperature @(rf/subscribe [::temperature scale])]
    [:div.input-group-prepend
     [:span.input-group-text {:style {:width :30px :text-align :center}} lbl]
     [:input.form-control
      {:type      "text"
       :on-focus  (fn [_] (rf/dispatch [::set-edit-scale scale]))
       :value     temperature
       :on-change (fn [e] (rf/dispatch [::set-temperature (.-value (.-target e)) scale]))}]]))

(defn main []
  [:div
   [:h2 "Task 2: Temperature Converter"]
   [:span
    [render-temp "F" :fahrenheit]
    [render-temp "C" :celsius]]
   gc2/about])

;; Events
(rf/reg-event-fx
  ::set-edit-scale
  (fn [{db :db} [_ scale]]
    {:db (-> db
             (assoc-in [db-key :edit-scale] scale)
             (update db-key dissoc :edit-text))}))

(rf/reg-event-fx
  ::set-temperature
  (fn [{db :db} [_ value scale]]
    (let [temperature (js/parseFloat value)]
      {:db
       (cond-> (assoc-in db [db-key :edit-text] value)
               (not (js/isNaN temperature))
               (assoc-in [db-key :temperature]
                         (case scale
                           :celsius temperature
                           :fahrenheit (gc2/f->c temperature))))})))

;; Subscriptions
(rf/reg-sub ::edit-scale (fn [db] (get-in db [db-key :edit-scale])))
(rf/reg-sub ::edit-text (fn [db] (get-in db [db-key :edit-text])))
(rf/reg-sub ::base-temp (fn [db] (get-in db [db-key :temperature])))

(rf/reg-sub
  ::temperature
  :<- [::edit-scale]
  :<- [::edit-text]
  :<- [::base-temp]
  (fn [[edit-scale edit-text temperature] [_ scale]]
    (when temperature
      (if (= scale edit-scale)
        edit-text
        (case scale
          :celsius temperature
          :fahrenheit (gc2/c->f temperature))))))
