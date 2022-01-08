(ns guis.gui02.views
  (:require [re-frame.core :as rf]))

(defn render-temp [lbl scale]
  (let [temperature (rf/subscribe [::temperature scale])]
    [:div.input-group-prepend
     [:span#basic-addon1.input-group-text {:style {:width :30px :text-align :center}} lbl]
     [:input.form-control
      {:type      "text"
       :on-focus  (fn [_] (rf/dispatch [::set-edit-scale scale]))
       ;:on-blur (fn [_] (js/console.log "BLUR"))
       :value     @temperature
       :on-change (fn [e] (rf/dispatch [::set-temperature (.-value (.-target e)) scale]))}]]))

(defn main []
  [:div
   [:h2 "Task 2: Temperature Converter"]
   [:span
    [render-temp "F" :fahrenheit]
    [render-temp "C" :celsius]]
   [:h5 "About"]
   [:p "Temperature converter"]
   [:ul
    [:li "Enter a Fahrenheit temperature and watch the Celsius value change."]
    [:li "Enter a Celsius temperature and watch the Fahrenheit value change."]]])

;; Events
(rf/reg-event-fx
  ::set-edit-scale
  (fn [{db :db} [_ scale]]
    {:db (-> db
             (assoc :edit-scale scale)
             (dissoc :edit-text))}))

(defn f->c [f]
  (/ (* (- f 32) 5) 9.0))

(defn c->f [c]
  (+ (/ (* c 9) 5) 32.0))

(rf/reg-event-fx
  ::set-temperature
  (fn [{db :db} [_ value scale]]
    (let [temperature (js/parseFloat value)]
      {:db
       (cond-> (assoc db :edit-text value)
               (not (js/isNaN temperature))
               (assoc :temperature
                      (case scale
                        :celsius temperature
                        :fahrenheit (f->c temperature))))})))

;; Subscriptions
(rf/reg-sub
  ::temperature
  (fn [{:keys [edit-scale edit-text temperature]} [_ scale]]
    (if (= scale edit-scale)
      edit-text
      (case scale
        :celsius temperature
        :fahrenheit (c->f temperature)))))
