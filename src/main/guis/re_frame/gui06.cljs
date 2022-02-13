(ns guis.re-frame.gui06
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.pprint :as pp]))

(defn get-point [event]
  (let [svg (.getElementById js/document "svg")
        pt (doto (.createSVGPoint svg)
             (#(-> % .-x (set! (.-clientX event))))
             (#(-> % .-y (set! (.-clientY event)))))
        p (.matrixTransform pt (-> svg .getScreenCTM .inverse))]
    [(.-x p) (.-y p)]))

(def db-key ::gui06)

;; Events
(rf/reg-event-fx
  ::initialize
  (fn [{db :db} _]
    {:db (update db db-key merge {:center          nil
                                  :radius          nil
                                  :circles         []
                                  :undo-redo-index 0
                                  :selected-index  nil})}))

(rf/reg-event-fx
  ::set-center
  (fn [{db :db} [_ center]]
    {:db (assoc-in db [db-key :center] center)}))

(rf/reg-event-fx
  ::set-radius
  (fn [{db :db} [_ radius]]
    {:db (assoc-in db [db-key :radius] radius)}))

(rf/reg-event-fx
  ::set-undo-redo-index
  (fn [{db :db} [_ undo-redo-index]]
    {:db (assoc-in db [db-key :undo-redo-index] undo-redo-index)}))

(rf/reg-event-fx
  ::set-selected-index
  (fn [{db :db} [_ selected-index]]
    (pp/pprint {::selected-index selected-index})
    {:db (assoc-in db [db-key :selected-index] selected-index)}))

(rf/reg-event-fx
  ::add-circle
  (fn [{db :db} _]
    {:db (update db db-key
                 (fn [{:keys [center radius undo-redo-index] :as s}]
                   (-> s
                       (dissoc :center :radius)
                       (update :circles subvec 0 undo-redo-index)
                       (update :circles conj {:center center :radius radius})
                       (update :undo-redo-index inc))))}))

(rf/reg-event-fx
  ::set-selected-radius
  (fn [{db :db} [_ radius]]
    {:db (update db db-key
                 (fn [{:keys [selected-index] :as state}]
                   (assoc-in state [:circles selected-index :radius] radius)))}))

(rf/reg-event-fx
  ::undo
  (fn [{:keys [db]} _]
    {:db (update-in db [db-key :undo-redo-index] #(max 0 (dec %)))}))

(rf/reg-event-fx
  ::redo
  (fn [{:keys [db]} _]
    (let [n (count (get-in db [db-key :circles]))]
      {:db (update-in db [db-key :undo-redo-index] #(min n (inc %)))})))

;; Subs
(rf/reg-sub ::center (fn [db] (get-in db [db-key :center])))
(rf/reg-sub ::radius (fn [db] (get-in db [db-key :radius])))
(rf/reg-sub ::circles (fn [db] (get-in db [db-key :circles])))
(rf/reg-sub ::undo-redo-index (fn [db] (get-in db [db-key :undo-redo-index])))
(rf/reg-sub ::selected-index (fn [db] (get-in db [db-key :selected-index])))
(rf/reg-sub
  ::selected-radius
  :<- [::circles]
  :<- [::selected-index]
  (fn [[circles selected-index]] (get-in circles [selected-index :radius])))

(defn main []
  (rf/dispatch [::initialize])
  (let [center (rf/subscribe [::center])
        radius (rf/subscribe [::radius])
        circles (rf/subscribe [::circles])
        undo-redo-index (rf/subscribe [::undo-redo-index])
        selected-index (rf/subscribe [::selected-index])
        selected-radius (rf/subscribe [::selected-radius])]
    (fn []
      [:div
       [:h2 "Task 6: Circle Drawer"]
       [:span
        [:svg#svg {:width 600 :height 400}
         [:rect {:x            0 :y 0 :width 600 :height 400 :stroke :black :fill :gray
                 :onMouseDown  (fn [e] (rf/dispatch [::set-center (get-point e)]))
                 :onMouseMove  (fn [e]
                                 (when @center
                                   (let [p (get-point e)
                                         [vx vy] (mapv - p @center)
                                         r (Math/sqrt (+ (* vx vx) (* vy vy)))]
                                     (rf/dispatch [::set-radius r]))))
                 :onMouseUp    #(rf/dispatch [::add-circle])
                 :onMouseLeave (fn [_]
                                 (rf/dispatch [::set-center nil])
                                 (rf/dispatch [::set-radius nil]))}]
         (doall
           (for [[index circle] (map vector (range @undo-redo-index) @circles)
                 :let [[cx cy] (:center circle)
                       radius (:radius circle)]]
             ^{:key (assoc circle :index index)}
             [:circle (cond-> {:cx       cx :cy cy :r radius
                               :stroke   :black :fill :green
                               :on-click #(rf/dispatch [::set-selected-index index])}
                              @center
                              (assoc :pointer-events :none))]))
         (let [[cx cy :as center] @center]
           (when (and center @radius)
             [:circle {:pointer-events :none :cx cx :cy cy :r @radius :stroke :black :fill :blue}]))]]
       (if @selected-index
         [:div.input-group-prepend
          [:span "Edit Radius:"]
          [:input
           {:style     {:width :auto}
            :type      "range"
            :min       0
            :value     @selected-radius
            :max       300
            :on-change #(rf/dispatch [::set-selected-radius (.-value (.-target %))])
            }]
          [:button.btn.btn-primary
           {:type     "button"
            :on-click #(rf/dispatch [::set-selected-index nil])}
           "Done"]]
         [:div
          [:button.btn.btn-primary
           {:type     "button"
            :disabled (zero? (or @undo-redo-index 0))
            :on-click #(rf/dispatch [::undo])}
           "Undo"]
          [:button.btn.btn-primary
           {:type     "button"
            :disabled (= (count @circles) @undo-redo-index)
            :on-click #(rf/dispatch [::redo])}
           "Redo"]])
       [:h5 "About"]
       [:p "Interactively draw circles with undo/redo history."]
       [:ul
        [:li "Click to start a circle and drag outward to size it."]
        [:li "Undo/redo shoud make sense."]
        [:li "Click a circle to get a slider bar to resize."]
        ]])))