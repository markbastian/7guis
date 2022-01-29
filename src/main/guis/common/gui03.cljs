(ns guis.common.gui03)

(def about
  [:span
   [:h5 "About"]
   [:p "Book some flights with constraints"]
   [:ul
    [:li "Note that one-way flights are the default."]
    [:li "If you adjust the departure greater than the return time, the return time will track with it."]
    [:li "Select return flight."]
    [:li "The return flight will always be constrained to be >= the depart time."]
    [:li "The Book button will give you a modal popup telling you your flight details."]]])