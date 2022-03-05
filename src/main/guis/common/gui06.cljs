(ns guis.common.gui06)

(def about
  [:span
   [:h5 "About"]
   [:p "Interactively draw circles with undo/redo history."]
   [:ul
    [:li "Click to start a circle and drag outward to size it."]
    [:li "Undo/redo shoud make sense."]
    [:li "Click a circle to get a slider bar to resize."]]])