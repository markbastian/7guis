(ns guis.reagent.core
  (:require [guis.reagent.gui01 :as gui01]
            [guis.reagent.gui02 :as gui02]
            [guis.reagent.gui03 :as gui03]
            [guis.reagent.gui04 :as gui04]
            [guis.reagent.gui05 :as gui05]
            [guis.reagent.gui06 :as gui06]
            [guis.reagent.gui07 :as gui07]))

(defn render []
  [:div
   [:ul.nav.nav-tabs.nav-fill.tabs-fixed-top
    [:li.nav-item [:a.nav-link.active {:data-toggle "tab" :href "#gui01"} [:h5 "Counter"]]]
    [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui02"} [:h5 "Temp"]]]
    [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui03"} [:h5 "Flight Booker"]]]
    [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui04"} [:h5 "Timer"]]]
    [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui05"} [:h5 "CRUD"]]]
    [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui06"} [:h5 "Circles"]]]
    [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui07"} [:h5 "Cells"]]]]
   [:div.tab-content
    [:div#gui01.tab-pane.container-fluid.active [gui01/render]]
    [:div#gui02.tab-pane.fade [gui02/render]]
    [:div#gui03.tab-pane.fade [gui03/render]]
    [:div#gui04.tab-pane.fade [gui04/render]]
    [:div#gui05.tab-pane.fade [gui05/render]]
    [:div#gui06.tab-pane.fade [gui06/render]]
    [:div#gui07.tab-pane.fade [gui07/render]]]])