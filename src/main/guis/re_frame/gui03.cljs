(ns guis.re-frame.gui03
  (:require [re-frame.core :as rf]
            [guis.common.gui03 :as gc3]))

(def db-key ::gui03)

;; Events
(rf/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    (let [today (subs (.toISOString (js/Date.)) 0 10)]
      {:db (assoc db db-key
                     {:booking-type :one-way
                      :depart-date  today
                      :return-date  today})})))

(rf/reg-event-fx
  ::booking-type
  (fn [{db :db} [_ booking-type]]
    {:db (assoc-in db [db-key :booking-type] booking-type)}))

(rf/reg-event-fx
  ::depart-date
  (fn [{db :db} [_ booking-type]]
    {:db (assoc-in db [db-key :depart-date] booking-type)}))

(rf/reg-event-fx
  ::return-date
  (fn [{db :db} [_ booking-type]]
    {:db (assoc-in db [db-key :return-date] booking-type)}))

;; Subscriptions
(rf/reg-sub ::booking-type (fn [db] (-> db db-key :booking-type)))
(rf/reg-sub ::depart-date (fn [db] (-> db db-key :depart-date)))
(rf/reg-sub ::return-date (fn [db] (-> db db-key :return-date)))
(rf/reg-sub ::initialized (fn [db] (some? (-> db db-key :booking-type))))

(defn invalid-flight? [{:keys [booking-type depart-date return-date]}]
  (and
    (= :return booking-type)
    (neg? (compare return-date depart-date))))

(defn flight-selector []
  [:select {:on-change (fn [e]
                         (rf/dispatch [::booking-type (-> e .-target .-value keyword)]))}
   [:option {:value :one-way} "one-way flight"]
   [:option {:value :return} "return flight"]])

(defn departure-selector []
  (let [booking-type @(rf/subscribe [::booking-type])
        depart-date @(rf/subscribe [::depart-date])
        return-date @(rf/subscribe [::return-date])]
    [:div.input-group-prepend
     [:span.input-group-text {:style {:width :90px}} "Depart:"]
     [:input.form-control
      {:type      "date"
       :value     depart-date
       :on-change (fn [e]
                    (let [v (.-value (.-target e))]
                      (if (neg? (compare return-date v))
                        (if (= :one-way booking-type)
                          (do
                            (rf/dispatch [::depart-date v])
                            (rf/dispatch [::return-date v]))
                          (js/alert "Depart date must be on or before return date!"))
                        (rf/dispatch [::depart-date v]))))}]]))

(defn return-selector []
  (let [booking-type @(rf/subscribe [::booking-type])
        depart-date @(rf/subscribe [::depart-date])
        return-date @(rf/subscribe [::return-date])]
    [:div.input-group-prepend
     [:span.input-group-text {:style {:width :90px}} "Return:"]
     [:input.form-control
      {:type      "date"
       :readOnly  (= :one-way booking-type)
       :value     return-date
       :on-change (fn [e]
                    (let [v (.-value (.-target e))]
                      (if (neg? (compare v depart-date))
                        (js/alert "Return date must be on or after depart date!")
                        (rf/dispatch [::return-date v]))))}]]))

(defn booking-modal []
  (let [booking-type @(rf/subscribe [::booking-type])
        depart-date @(rf/subscribe [::depart-date])
        return-date @(rf/subscribe [::return-date])]
    (when (or
            (and depart-date (= :one-way booking-type))
            (and depart-date return-date))
      [:span
       [:div#flightBookingModal.modal.fade
        {:tabIndex "-1" :role "dialog"}
        [:div.modal-dialog {:role "document"}
         [:div.modal-content
          [:div.modal-header
           [:h5.modal-title "Flight Booked"]]
          [:div.modal-body (if (= :one-way booking-type)
                             (str "You have booked a one-way flight on " depart-date ".")
                             (str "You have booked a round-trip flight, departing on "
                                  depart-date " and returning on " return-date "."))]
          [:div.modal-footer
           [:button.btn.btn-secondary {:type "button" :data-dismiss "modal"} "Close"]]]]]
       [:button.btn.btn-primary
        {:type        "button"
         :data-toggle "modal"
         :data-target "#flightBookingModal"}
        "Book"]])))

(defn ^:export main []
  (rf/dispatch [::initialize])
  (fn []
    [:div
     [:h2 "Task 3: Flight Booker"]
     [:span
      [flight-selector]
      [departure-selector]
      [return-selector]
      [booking-modal]]
     gc3/about]))
