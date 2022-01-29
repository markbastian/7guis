;(shadow.cljs.devtools.api/nrepl-select :frontend)
(ns guis.main
  (:require [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]
            [guis.reagent.core :as rg.7guis]
            [guis.re-frame.core :as rf.7guis]
            [reagent.dom :as rd]
            [re-frame.core :as re-frame]))

(defn home-page []
  [:div
   [:h2 "Welcome to 7 GUIs"]])

(defn about-page []
  [:div
   [:h2 "7 GUI Tasks"]
   [:h3 "by Mark Bastian"]])

(defn current-page []
  (let [route-name @(re-frame/subscribe [::route-name])
        route-view @(re-frame/subscribe [::route-view])]
    [:div
     [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
      [:a.navbar-brand {:href "#"} "7 GUIs"]
      [:button.navbar-toggler {:type "button" :data-toggle "collapse"}
       [:span.navbar-toggler-icon]]
      [:div.collapse.navbar-collapse
       [:div.navbar-nav
        [:a.nav-item.nav-link {:href (rfe/href ::home) :active (str (= ::home route-name))} "Home"]
        [:a.nav-item.nav-link {:href (rfe/href ::reagent) :active (str (= ::reagent route-name))} "Reagent"]
        [:a.nav-item.nav-link {:href (rfe/href ::re-frame) :active (str (= ::re-frame route-name))}  "Re-Frame"]
        [:a.nav-item.nav-link {:href (rfe/href ::about) :active (str (= ::about route-name))}  "About"]]]]
     (when route-view
       (route-view))]))

(def routes
  [["/"
    {:name ::home
     :view home-page}]
   ["/about"
    {:name ::about
     :view about-page}]
   ["/reagent"
    {:name ::reagent
     :view rg.7guis/render}]
   ["/re-frame"
    {:name ::re-frame
     :view rf.7guis/render}]])

;; Events
(re-frame/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    {:db (assoc db :clicks 0)}))

(re-frame/reg-event-fx
  ::route
  (fn [{db :db} [_ route]]
    {:db (assoc db :route route)}))

;; Subscriptions
(re-frame/reg-sub ::route (fn [db] (:route db)))

(re-frame/reg-sub ::route-name (fn [db] (-> db :route :data :name)))
(re-frame/reg-sub ::route-view (fn [db] (-> db :route :data :view)))

;;https://github.com/metosin/reitit/blob/master/examples/frontend/src/frontend/core.cljs
(defn init []
  (rfe/start!
    (rf/router routes {:data {:coercion rss/coercion}})
    (fn [m]
      (re-frame/dispatch [::route m]))
    ;; set to false to enable HistoryAPI
    {:use-fragment true})
  (rd/render [current-page] (.getElementById js/document "ui-root")))

(defn ^:dev/after-load ui-root [] (init))

