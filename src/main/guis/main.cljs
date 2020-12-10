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
   [gui01/render]
   [gui02/render]
   [gui03/render]
   [gui04/render]
   [gui05/render]
   [gui06/render]
   [gui07/render]
   ])

(defn ^:dev/after-load ui-root []
  (rd/render [render] (.getElementById js/document "ui-root")))

(defn init []
  (let [root (.getElementById js/document "ui-root")]
    (.log js/console root)
    (rd/render [render] root)))