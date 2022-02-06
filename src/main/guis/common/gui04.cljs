(ns guis.common.gui04)

(def about
  [:span
   [:h5 "About"]
   [:p "Stateful timer stuff"]
   [:ul
    [:li "Press reset if the time has run to the end of the progress bar."]
    [:li "Watch the elapsed time elapse."]
    [:li "Drag the duration slider to modify the duration, seeing the progress bar move inversely with the duration."]
    [:li "Note that the time will be capped to your duration."]
    [:li "Make the duration longer and the time will keep rolling forward."]
    [:li "Reset as desired."]]])