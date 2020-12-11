(ns guis.gui02
  (:require [reagent.core :as r]))

(defn f->c [f]
  (/ (* (- f 32) 5) 9.0))

(defn c->f [c]
  (+ (/ (* c 9) 5) 32.0))

(defn update-temp [xform temperature-celsius s]
  (if (seq s)
    (let [celsius (xform (js/parseFloat s))]
      (when-not
        (or
          (= @temperature-celsius celsius)
          (js/isNaN celsius))
        (reset! temperature-celsius celsius)))
    (reset! temperature-celsius nil)))

(def update-celsius (partial update-temp identity))
(def update-fahrenheit (partial update-temp f->c))

(defn add-temp-checker [temperature-state]
  (add-watch :valid-temps temperature-state
             (fn [_ _ o n]
               (when-not (= o n)
                 (if n (max n -273.15) n)))))

;Fix bare - sign
(defn render []
  (let [temperature-state (r/atom nil)]
    (fn []
      [:div
       [:h2 "Task 2: Temperature Converter"]
       [:span
        [:div.input-group-prepend
         [:span#basic-addon1.input-group-text {:style {:width :30px :text-align :center}} "F"]
         [:input.form-control
          {:type      "text"
           :value     (some-> @temperature-state c->f)
           :on-change (fn [e] (update-fahrenheit
                                temperature-state
                                (.-value (.-target e))))}]]
        [:div.input-group-prepend
         [:span#basic-addon1.input-group-text {:style {:width :30px :text-align :center}} "C"]
         [:input.form-control
          {:type      "text"
           :value     (some-> @temperature-state)
           :on-change (fn [e] (update-celsius
                                temperature-state
                                (.-value (.-target e))))}]]]
       [:h5 "About"]
       [:p "Temperature converter"]
       [:ul
        [:li "Enter a Fahrenheit temperature and watch the Celsius value change."]
        [:li "Enter a Celsius temperature and watch the Fahrenheit value change."]]])))