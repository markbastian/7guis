;(shadow.cljs.devtools.api/nrepl-select :frontend)
(ns guis.main
  (:require [guis.gui01 :as gui01]
            [guis.gui02 :as gui02]
            [guis.gui03 :as gui03]
            [guis.gui04 :as gui04]
            [guis.gui05 :as gui05]
            [guis.gui06 :as gui06]
            [guis.gui07 :as gui07]
            [reagent.dom :as rd]))

(defn render []
  [:div
   [:h1 "7 GUI Tasks"]
   [:h3 "by Mark Bastian"]
   [:div
    [:ul.nav.nav-tabs.nav-fill.tabs-fixed-top
     [:li.nav-item [:a.nav-link.active {:data-toggle "tab" :href "#gui01"} [:h4 "Counter"]]]
     [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui02"} [:h4 "Temp"]]]
     [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui03"} [:h4 "Flight Booker"]]]
     [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui04"} [:h4 "Timer"]]]
     [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui05"} [:h4 "CRUD"]]]
     [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui06"} [:h4 "Circles"]]]
     [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#gui07"} [:h4 "Cells"]]]]
    [:div.tab-content
     [:div#gui01.tab-pane.container-fluid.active [gui07/render]]
     [:div#gui02.tab-pane.fade [gui02/render]]
     [:div#gui03.tab-pane.fade [gui03/render]]
     [:div#gui04.tab-pane.fade [gui04/render]]
     [:div#gui05.tab-pane.fade [gui05/render]]
     [:div#gui06.tab-pane.fade [gui06/render]]
     [:div#gui07.tab-pane.fade [gui01/render]]]]])

(defn ^:dev/after-load ui-root []
  (rd/render [render] (.getElementById js/document "ui-root")))

(defn init []
  (let [root (.getElementById js/document "ui-root")]
    (.log js/console root)
    (rd/render [render] root)))