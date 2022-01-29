(ns guis.common.gui02)

(defn f->c [f]
  (/ (* (- f 32) 5) 9.0))

(defn c->f [c]
  (+ (/ (* c 9) 5) 32.0))

(def about
  [:span
   [:h5 "About"]
   [:p "Temperature converter"]
   [:ul
    [:li "Enter a Fahrenheit temperature and watch the Celsius value change."]
    [:li "Enter a Celsius temperature and watch the Fahrenheit value change."]]])