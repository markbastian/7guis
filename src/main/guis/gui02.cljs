(ns guis.gui02
  (:require [reagent.core :as r]))

(defn f->c [f]
  (/ (* (- f 32) 5) 9.0))

(defn c->f [c]
  (+ (/ (* c 9) 5) 32.0))

(defn gain-focus [e state scale]
  (let [v (.-value (.-target e))]
    (swap! state assoc :editing scale :text v)))

(defn value [state scale xform]
  (let [{:keys [text]} @state]
    (if (= scale (:editing @state))
      text
      (let [temp (js/parseFloat text)]
        (when-not (js/isNaN temp)
          (xform temp))))))

(defn change [e state]
  (let [v (.-value (.-target e))]
    (swap! state assoc :text v)))

(defn render-temp [state lbl scale xform]
  [:div.input-group-prepend
   [:span#basic-addon1.input-group-text {:style {:width :30px :text-align :center}} lbl]
   [:input.form-control
    {:type      "text"
     :on-focus  (fn [e] (gain-focus e state scale))
     :value     (value state scale xform)
     :on-change (fn [e] (change e state))}]])

(defn render []
  (let [state (r/atom nil)]
    (fn []
      [:div
       [:h2 "Task 2: Temperature Converter"]
       [:span
        [render-temp state "F" :fahrenheit c->f]
        [render-temp state "C" :celsius f->c]]
       [:h5 "About"]
       [:p "Temperature converter"]
       [:ul
        [:li "Enter a Fahrenheit temperature and watch the Celsius value change."]
        [:li "Enter a Celsius temperature and watch the Fahrenheit value change."]]])))