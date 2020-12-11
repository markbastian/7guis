(ns guis.gui06
  (:require [reagent.core :as r]))

(defn get-point [event]
  (let [svg (.getElementById js/document "svg")
        pt (doto (.createSVGPoint svg)
             (#(-> % .-x (set! (.-clientX event))))
             (#(-> % .-y (set! (.-clientY event)))))
        p (.matrixTransform pt (-> svg .getScreenCTM .inverse))]
    [(.-x p) (.-y p)]))

(defn render []
  (let [state (r/atom {:circles         []
                       :undo-redo-index 0})]
    (fn []
      [:div
       [:h2 "Task 6: Circle Drawer"]
       [:span
        [:svg#svg {:width 600 :height 400}
         [:rect {:x            0 :y 0 :width 600 :height 400 :stroke :black :fill :gray
                 :onMouseDown  (fn [e] (swap! state assoc :center (get-point e)))
                 :onMouseMove  (fn [e]
                                 (when-some [center (:center @state)]
                                   (let [p (get-point e)
                                         [vx vy] (mapv - p center)
                                         r (Math/sqrt (+ (* vx vx) (* vy vy)))]
                                     (swap! state assoc :radius r))))
                 :onMouseUp    (fn [e] (swap! state
                                              (fn [{:keys [undo-redo-index circles] :as s}]
                                                (let [circles (subvec circles 0 undo-redo-index)]
                                                  (-> s
                                                      (dissoc :center :radius)
                                                      (update :circles subvec 0 undo-redo-index)
                                                      (update :circles conj (select-keys s [:center :radius]))
                                                      (update :undo-redo-index inc))))))
                 :onMouseLeave (fn [e] (swap! state dissoc :center :radius))}]
         (doall
           (let [{:keys [undo-redo-index circles]} @state]
             (for [[index circle] (map vector (range undo-redo-index) circles)
                   :let [[cx cy] (:center circle)
                         radius (:radius circle)]]
               ^{:key (assoc circle :index index)}
               [:circle (cond-> {:cx       cx :cy cy :r radius
                                 :stroke   :black :fill :green
                                 :on-click (fn [e] (swap! state assoc :selected-index index))}
                                (:center @state)
                                (assoc :pointer-events :none))])))
         (let [[cx cy :as center] (:center @state) radius (:radius @state)]
           (when (and center radius)
             [:circle {:pointer-events :none :cx cx :cy cy :r radius :stroke :black :fill :blue}]))]]
       (if-some [selected-index (:selected-index @state)]
         [:div.input-group-prepend
          [:span "Edit Radius:"]
          [:input
           {:style     {:width :auto}
            :type      "range"
            :min       0
            :value     (get-in @state [:circles selected-index :radius])
            :max       300
            :on-change (fn [e]
                         (let [v (.-value (.-target e))]
                           (swap! state assoc-in [:circles selected-index :radius] v)))
            }]
          [:button.btn.btn-primary
           {:type     "button"
            :on-click (fn [_] (swap! state dissoc :selected-index))}
           "Done"]]
         [:div
          [:button.btn.btn-primary
           {:type     "button"
            :on-click (fn [_]
                        (swap! state
                               (fn [{:keys [undo-redo-index] :as s}]
                                 (assoc s :undo-redo-index (max 0 (dec undo-redo-index))))))}
           "Undo"]
          [:button.btn.btn-primary
           {:type     "button"
            :on-click (fn [_]
                        (swap! state
                               (fn [{:keys [undo-redo-index circles] :as s}]
                                 (assoc s :undo-redo-index
                                          (min (count circles) (inc undo-redo-index))))))}
           "Redo"]])
       [:h5 "About"]
       [:p "Interactively draw circles with undo/redo history."]
       [:ul
        [:li "Click to start a circle and drag outward to size it."]
        [:li "Undo/redo shoud make sense."]
        [:li "Click a circle to get a slider bar to resize."]
        ]])))