(ns guis.re-frame.gui05
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.string :as cs]
            [guis.common.gui05 :as gc05]))

(def db-key ::gui05)

;; Events
(rf/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    {:db (assoc db db-key
                   {:first-name     ""
                    :surname        ""
                    :prefilter      ""
                    :selected-index nil
                    :names          []})}))

(rf/reg-event-fx
  ::set-state-key
  (fn [{db :db} [_ k v]]
    {:db (assoc-in db [db-key k] v)}))

(rf/reg-event-fx
  ::create-name
  (fn [{db :db} [_ first-name surname]]
    {:db (update-in db [db-key :names] conj {:first-name first-name :surname surname})}))

;; Subs
(rf/reg-sub ::state-key (fn [db [_ k]] (get-in db [db-key k])))
(rf/reg-sub ::first-name (fn [db] (get-in db [db-key :first-name])))
(rf/reg-sub ::surname (fn [db] (get-in db [db-key :surname])))
(rf/reg-sub ::prefilter (fn [db] (get-in db [db-key :prefilter])))
(rf/reg-sub ::selected-index (fn [db] (get-in db [db-key :selected-index])))
(rf/reg-sub ::names (fn [db] (get-in db [db-key :names])))

(defn input-box [state-key]
  (let [value @(rf/subscribe [::state-key state-key])]
    [:input.form-control
     {:type      "text"
      :value     value
      :on-change (fn [e]
                   (let [v (.-value (.-target e))]
                     (rf/dispatch [::set-state-key state-key v])))}]))

(defn main []
  (rf/dispatch [::initialize])
  (fn []
    (let [first-name @(rf/subscribe [::first-name])
          surname @(rf/subscribe [::surname])
          prefilter @(rf/subscribe [::prefilter])
          selected-index @(rf/subscribe [::selected-index])
          names @(rf/subscribe [::names])
          state nil
          ]
      [:div
       [:h2 "Task 5: CRUD"]
       [:span
        [:div.input-group-prepend
         [:span.input-group-text {:style {:width :130px}} "Filter Prefix"]
         [input-box :prefilter]]
        [:div.input-group-prepend
         [:span.input-group-text {:style {:width :130px}} "Name:"]
         [input-box :first-name]]
        [:div.input-group-prepend
         [:span.input-group-text {:style {:width :130px}} "Surname:"]
         [input-box :surname]]
        [:ul.list-group
           (doall
             (for [[idx {:keys [first-name surname] :as item}] (map vector (range) names)
                   :let [txt (str surname ", " first-name)]
                   :when (cs/starts-with? txt prefilter)]
               ^{:key (assoc item :index idx)}
               [(if (= idx selected-index)
                  :li.list-group-item.active
                  :li.list-group-item)
                {:on-click (fn [_]
                             ;; Perhaps if selecting the state have a downstream
                             ;; effect to set the name and surname to this idx
                             (rf/dispatch [::set-state-key :selected-index idx])
                             ;;TODO - Also set the first name and surname
                             #_(swap! state
                                      (fn [v]
                                        (-> v
                                            (assoc :selected-index idx)
                                            (into item)))))}
                txt]))]
        [:span
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_]
                       (let [n {:first-name first-name :surname surname}]
                         (rf/dispatch [::create-name first-name surname])
                         #_(swap! state
                                  (fn [v]
                                    (-> v
                                        (update :names conj n)
                                        (dissoc :selected-index))))))}
          "Create"]
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_]
                       (when selected-index
                         #_(swap! state assoc-in [:names selected-index]
                                  (select-keys @state [:first-name :surname]))))}
          "Update"]
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_]
                       (letfn [(remove-at [names selected-index]
                                 (let [[pre post] (split-at selected-index names)]
                                   (into (vec pre) (rest post))))]
                         (when selected-index
                           #_(swap! state
                                    (fn [st]
                                      (-> st
                                          (update :names remove-at selected-index)
                                          (dissoc :selected-index)))))))}
          "Delete"]]]
       gc05/about])))