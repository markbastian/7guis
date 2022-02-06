(ns guis.re-frame.core
  (:require [guis.re-frame.gui01 :as gui01]
            [guis.re-frame.gui02 :as gui02]
            [guis.re-frame.gui03 :as gui03]
            [guis.re-frame.gui04 :as gui04]
            [guis.re-frame.gui05 :as gui05]
            [guis.reagent.gui06 :as gui06]
            [guis.reagent.gui07 :as gui07]))

;https://juju.one/complete-re-frame-tutorial/
;https://purelyfunctional.tv/guide/re-frame-building-blocks/

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
    [:div#gui01.tab-pane.container-fluid.active [gui01/main]]
    [:div#gui02.tab-pane.fade [gui02/main]]
    [:div#gui03.tab-pane.fade [gui03/main]]
    [:div#gui04.tab-pane.fade [gui04/main]]
    [:div#gui05.tab-pane.fade [gui05/main]]
    [:div#gui06.tab-pane.fade [gui06/render]]
    [:div#gui07.tab-pane.fade [gui07/render]]]])