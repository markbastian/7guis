(ns guis.gui03
  (:require [reagent.core :as r]))

(defn invalid-flight? [{:keys [booking-type depart-date return-date]}]
  (and
    (= :return booking-type)
    (neg? (compare return-date depart-date))))

(defn render []
  (let [state (let [today (subs (.toISOString (js/Date.)) 0 10)]
                (r/atom {:booking-type :one-way
                         :depart-date  today
                         :return-date  today}))]
    (fn []
      [:div
       [:h2 "Task 3: Flight Booker"]
       [:span
        [:select#flight {:name      "flight"
                         :on-change (fn [e]
                                      (let [v (.-value (.-target e))]
                                        (swap! state assoc :booking-type (keyword v))))}
         [:option {:value :one-way} "one-way flight"]
         [:option {:value :return} "return flight"]]
        [:div.input-group-prepend
         [:span#basic-addon1.input-group-text {:style {:width :90px}} "Depart:"]
         [:input.form-control
          {:type      "date"
           :value     (:depart-date @state)
           :on-change (fn [e]
                        (let [v (.-value (.-target e))]
                          (if (neg? (compare (:return-date @state) v))
                            (if (= :one-way (:booking-type @state))
                              (swap! state assoc :return-date v :depart-date v)
                              (js/alert "Depart date must be on or before return date!"))
                            (swap! state assoc :depart-date v))))
           }]]
        [:div.input-group-prepend {}
         [:span#basic-addon1.input-group-text {:style {:width :90px}} "Return:"]
         [:input.form-control
          {:type      "date"
           :readOnly  (= :one-way (:booking-type @state))
           :value     (:return-date @state)
           :on-change (fn [e]
                        (let [v (.-value (.-target e))]
                          (if (neg? (compare v (:depart-date @state)))
                            (js/alert "Return date must be on or after depart date!")
                            (swap! state assoc :return-date v))))
           }]]
        (let [{:keys [booking-type depart-date return-date]} @state]
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
              [:button.btn.btn-secondary {:type "button" :data-dismiss "modal"} "Close"]]]]])
        [:button.btn.btn-primary
         {:type        "button"
          :data-toggle "modal"
          :data-target "#flightBookingModal"}
         "Book"]]
       [:h5 "About"]
       [:p "Book some flights with constraints"]
       [:ul
        [:li "Note that one-way flights are the default."]
        [:li "If you adjust the departure greater than the return time, the return time will track with it."]
        [:li "Select return flight."]
        [:li "The return flight will always be constrained to be >= the depart time."]
        [:li "The Book button will give you a modal popup telling you your flight details."]]])))