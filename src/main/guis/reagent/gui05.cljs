(ns guis.reagent.gui05
  (:require [reagent.core :as r]
            [clojure.string :as cs]
            [guis.common.gui05 :as gc05]))

(defn input-box [state-key state]
  [:input.form-control
   {:type      "text"
    :value     (state-key @state)
    :on-change (fn [e]
                 (let [v (.-value (.-target e))]
                   (swap! state assoc state-key v)))}])

(defn render []
  (let [state (r/atom {:first-name ""
                       :surname    ""
                       :prefilter  ""
                       :names      []})]
    (fn []
      [:div
       [:h2 "Task 5: CRUD"]
       [:span
        [:div.input-group-prepend
         [:span#basic-addon1.input-group-text {:style {:width :130px}} "Filter Prefix"]
         [input-box :prefilter state]]
        [:div.input-group-prepend
         [:span#basic-addon1.input-group-text {:style {:width :130px}} "Name:"]
         [input-box :first-name state]]
        [:div.input-group-prepend
         [:span#basic-addon1.input-group-text {:style {:width :130px}} "Surname:"]
         [input-box :surname state]]
        [:ul.list-group
         (doall
           (for [[idx {:keys [first-name surname] :as item}] (map vector (range) (:names @state))
                 :let [txt (str surname ", " first-name)]
                 :when (cs/starts-with? txt (:prefilter @state))]
             ^{:key (assoc item :index idx)}
             [(if (= idx (:selected-index @state))
                :li.list-group-item.active
                :li.list-group-item)
              {:on-click (fn [_]
                           (swap! state
                                  (fn [v]
                                    (-> v
                                        (assoc :selected-index idx)
                                        (into item)))))}
              txt]))]
        [:span
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_]
                       (let [n (select-keys @state [:first-name :surname])]
                         (swap! state
                                (fn [v]
                                  (-> v
                                      (update :names conj n)
                                      (dissoc :selected-index))))))}
          "Create"]
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_]
                       (when-some [selected-index (:selected-index @state)]
                         (swap! state assoc-in [:names selected-index]
                                (select-keys @state [:first-name :surname]))))}
          "Update"]
         [:button.btn.btn-primary
          {:type     "button"
           :on-click (fn [_]
                       (letfn [(remove-at [names selected-index]
                                 (let [[pre post] (split-at selected-index names)]
                                   (into (vec pre) (rest post))))]
                         (when-some [selected-index (:selected-index @state)]
                           (swap! state
                                  (fn [st]
                                    (-> st
                                        (update :names remove-at selected-index)
                                        (dissoc :selected-index)))))))}
          "Delete"]]]
       gc05/about])))