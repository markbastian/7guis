(ns guis.gui04
  (:require [reagent.core :as r]))

(def ms-per-sec 1000)
(def max-duration 100)

(defn reset-state! [state]
  (let [{:keys [duration-seconds]} @state
        t (js/Date.)]
    (reset!
      state
      {:duration-seconds (or duration-seconds (/ max-duration 2))
       :start-time       t
       :current-time     t
       :dt-seconds       0})))

(defn update-dt [state]
  (let [{:keys [start-time duration-seconds]} @state
        max-time (+ (.getTime start-time) (* ms-per-sec duration-seconds))
        current-time (js/Date. (min max-time (.getTime (js/Date.))))
        dt-seconds (/ (- current-time start-time) ms-per-sec)]
    (swap! state assoc
           :current-time current-time
           :dt-seconds dt-seconds)))

(def state (doto (r/atom {}) reset-state!))

;Sadly, IDK if there's a way to define a single time in the render block,
;hence the external state.
(defonce timer (js/setInterval #(update-dt state) 100))

(defn render []
  (fn []
    (let [{:keys [dt-seconds duration-seconds]} @state]
      [:div
       [:h2 "Task 4: Timer"]
       [:span
        [:div.input-group-prepend
         [:span {:style {:width :120px}} "Progress:"]
         [:progress {:style {:width :100%}
                     :value dt-seconds
                     :max (:duration-seconds @state)}]]
        [:div.input-group-prepend
         [:span
          [:label {:style {:width :120px}} "Elapsed Time:"]
          [:label (str dt-seconds "/" duration-seconds "s")]]]
        [:div.input-group-prepend
         [:span {:style {:width :120px}} "Duration: "]
         [:input {:style {:width :100%}
                  :type      "range"
                  :min       0
                  :value     (:duration-seconds @state)
                  :max       max-duration
                  :on-change (fn [e]
                               (let [v (.-value (.-target e))]
                                 (swap! state assoc :duration-seconds v)))}]]
        [:div.input-group-prepend
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_] (reset-state! state))}
          "Reset"]]]
       [:h5 "About"]
       [:p "Stateful timer stuff"]
       [:ul
        [:li "Press reset if the time has run to the end of the progress bar."]
        [:li "Watch the elapsed time elapse."]
        [:li "Drag the duration slider to modify the duration, seeing the progress bar move inversely with the duration."]
        [:li "Note that the time will be capped to your duration."]
        [:li "Make the duration longer and the time will keep rolling forward."]
        [:li "Reset as desired."]]])))
