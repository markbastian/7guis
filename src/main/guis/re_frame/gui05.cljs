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
  ::select-individual
  (fn [{db :db} [_ first-name surname index]]
    {:db (update-in db [db-key] merge {:first-name     first-name
                                       :surname        surname
                                       :selected-index index})}))

(rf/reg-event-fx
  ::create-name
  (fn [{db :db} [_ first-name surname]]
    {:db (-> db
             (update-in [db-key :names] conj {:first-name first-name :surname surname})
             (update-in [db-key] dissoc :selected-index))}))

(rf/reg-event-fx
  ::update-individual
  (fn [{db :db} [_ first-name surname index]]
    {:db (assoc-in db [db-key :names index] {:first-name first-name :surname surname})}))

(rf/reg-event-fx
  ::delete-individual
  (fn [{db :db} [_ index]]
    (letfn [(remove-at [names selected-index]
              (let [[pre post] (split-at selected-index names)]
                (into (vec pre) (rest post))))]
      {:db (-> db
               (update-in [db-key :names] remove-at index)
               (update-in [db-key] dissoc
                          :selected-index
                          :first-name
                          :surname))})))

;; Subs
(rf/reg-sub ::state-key (fn [db [_ k]] (get-in db [db-key k])))
(rf/reg-sub ::first-name (fn [db] (get-in db [db-key :first-name])))
(rf/reg-sub ::surname (fn [db] (get-in db [db-key :surname])))
(rf/reg-sub ::prefilter (fn [db] (get-in db [db-key :prefilter])))
(rf/reg-sub ::selected-index (fn [db] (get-in db [db-key :selected-index])))
(rf/reg-sub ::names (fn [db] (get-in db [db-key :names])))
(rf/reg-sub
  ::selected-name
  :<- [::selected-index]
  :<- [::names]
  (fn [[selected-index names]]
    (get names selected-index)))

(defn input-box [state-key]
  (let [value @(rf/subscribe [::state-key state-key])]
    [:input.form-control
     {:type      "text"
      :value     value
      :on-change (fn [e]
                   (let [v (.-value (.-target e))]
                     (rf/dispatch [::set-state-key state-key v])))}]))

(defn name-list []
  (let [prefilter @(rf/subscribe [::prefilter])
        selected-index @(rf/subscribe [::selected-index])
        names @(rf/subscribe [::names])]
    [:ul.list-group
     (doall
       (for [[idx {:keys [first-name surname] :as item}] (map vector (range) names)
             :let [txt (str surname ", " first-name)]
             :when (cs/starts-with? txt prefilter)]
         ^{:key (assoc item :index idx)}
         [(if (= idx selected-index)
            :li.list-group-item.active
            :li.list-group-item)
          {:on-click #(rf/dispatch [::select-individual first-name surname idx])}
          txt]))]))

(defn create-button []
  (let [first-name @(rf/subscribe [::first-name])
        surname @(rf/subscribe [::surname])]
    [:button.btn.btn-primary
     {:type     "button"
      :disabled (or
                  (empty? first-name)
                  (empty? surname))
      :on-click #(rf/dispatch [::create-name first-name surname])}
     "Create"]))

(defn update-button []
  (let [first-name @(rf/subscribe [::first-name])
        surname @(rf/subscribe [::surname])
        selected-index @(rf/subscribe [::selected-index])
        {selected-first-name :first-name selected-surname :surname} @(rf/subscribe [::selected-name])]
    [:button.btn.btn-primary
     {:type     "button"
      :disabled (or
                  (nil? selected-index)
                  (and
                    (= selected-first-name first-name)
                    (= selected-surname surname)))
      :on-click #(rf/dispatch [::update-individual first-name surname selected-index])}
     "Update"]))

(defn delete-button []
  (let [selected-index @(rf/subscribe [::selected-index])]
    [:button.btn.btn-primary
     {:type     "button"
      :disabled (nil? selected-index)
      :on-click #(rf/dispatch [::delete-individual selected-index])}
     "Delete"]))

(defn main []
  (rf/dispatch [::initialize])
  (fn []
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
      [name-list]
      [:span
       [create-button]
       [update-button]
       [delete-button]]]
     gc05/about]))