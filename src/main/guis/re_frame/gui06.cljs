(ns guis.re-frame.gui06
  (:require [re-frame.core :as rf]
            [guis.common.gui06 :as gc06]))

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
                                  :history         [[]]
                                  :undo-redo-index 0
                                  :edit-index      nil
                                  :edit-radius     nil})}))

(rf/reg-event-fx
  ::set-center
  (fn [{db :db} [_ center]]
    {:db (assoc-in db [db-key :center] center)}))

(rf/reg-event-fx
  ::set-radius
  (fn [{db :db} [_ radius]]
    {:db (assoc-in db [db-key :radius] radius)}))

(rf/reg-event-fx
  ::begin-editing
  (fn [{db :db} [_ edit-index edit-radius]]
    {:db (update db db-key merge {:edit-index  edit-index
                                  :edit-radius edit-radius})}))

(rf/reg-event-fx
  ::add-circle
  (fn [{db :db} [_ center radius]]
    {:db (update db db-key
                 (fn [{:keys [undo-redo-index history] :as s}]
                   (let [c (conj (history undo-redo-index) {:center center :radius radius})]
                     (-> s
                         (dissoc :center :radius)
                         (update :history subvec 0 (inc undo-redo-index))
                         (update :history conj c)
                         (update :undo-redo-index inc)))))}))

(rf/reg-event-fx
  ::update-circle
  (fn [{db :db} [_ edit-index radius]]
    {:db (update db db-key
                 (fn [{:keys [undo-redo-index history] :as s}]
                   (let [c (-> (history undo-redo-index)
                               (assoc-in [edit-index :radius] radius))]
                     (-> s
                         (dissoc :edit-index :edit-radius)
                         (update :history subvec 0 (inc undo-redo-index))
                         (update :history conj c)
                         (update :undo-redo-index inc)))))}))

(rf/reg-event-fx
  ::set-edit-radius
  (fn [{db :db} [_ radius]]
    {:db (assoc-in db [db-key :edit-radius] radius)}))

(rf/reg-event-fx
  ::undo
  (fn [{:keys [db]} _]
    {:db (update-in db [db-key :undo-redo-index] #(max 0 (dec %)))}))

(rf/reg-event-fx
  ::redo
  (fn [{:keys [db]} _]
    (let [n (count (get-in db [db-key :history]))]
      {:db (update-in db [db-key :undo-redo-index] #(min n (inc %)))})))

;; Subs
(rf/reg-sub ::center (fn [db] (get-in db [db-key :center])))
(rf/reg-sub ::radius (fn [db] (get-in db [db-key :radius])))
(rf/reg-sub ::history (fn [db] (get-in db [db-key :history])))
(rf/reg-sub ::undo-redo-index (fn [db] (get-in db [db-key :undo-redo-index])))
(rf/reg-sub ::edit-index (fn [db] (get-in db [db-key :edit-index])))
(rf/reg-sub ::edit-radius (fn [db] (get-in db [db-key :edit-radius])))

(rf/reg-sub
  ::circles
  :<- [::history]
  :<- [::undo-redo-index]
  (fn [[history undo-redo-index]]
    (get history undo-redo-index)))

(defn radius-editor []
  (let [edit-index (rf/subscribe [::edit-index])
        edit-radius (rf/subscribe [::edit-radius])]
    [:div.input-group-prepend
     [:span "Edit Radius:"]
     [:input
      {:style     {:width :auto}
       :type      "range"
       :min       0
       :value     @edit-radius
       :max       300
       :on-change #(rf/dispatch [::set-edit-radius (.-value (.-target %))])}]
     [:button.btn.btn-primary
      {:type     "button"
       :on-click #(rf/dispatch [::update-circle @edit-index @edit-radius])}
      "Done"]]))

(defn undo-redo-buttons []
  (let [history (rf/subscribe [::history])
        undo-redo-index (rf/subscribe [::undo-redo-index])]
    [:div
     [:button.btn.btn-primary
      {:type     "button"
       :disabled (zero? @undo-redo-index)
       :on-click #(rf/dispatch [::undo])}
      "Undo"]
     [:button.btn.btn-primary
      {:type     "button"
       :disabled (= (inc @undo-redo-index) (count @history))
       :on-click #(rf/dispatch [::redo])}
      "Redo"]]))

(defn draw-active-circle []
  (let [center (rf/subscribe [::center])
        radius (rf/subscribe [::radius])
        [cx cy :as center] @center]
    (when (and center @radius)
      [:circle {:pointer-events :none :cx cx :cy cy :r @radius :stroke :black :fill :blue}])))

(defn main []
  (rf/dispatch [::initialize])
  (let [center (rf/subscribe [::center])
        radius (rf/subscribe [::radius])
        circles (rf/subscribe [::circles])
        edit-index (rf/subscribe [::edit-index])
        edit-radius (rf/subscribe [::edit-radius])]
    (fn []
      [:div
       [:h2 "Task 6: Circle Drawer"]
       [:svg#svg {:width 600 :height 400}
        [:rect {:x            0 :y 0 :width 600 :height 400 :stroke :black :fill :gray
                :onMouseDown  (fn [e] (rf/dispatch [::set-center (get-point e)]))
                :onMouseMove  (fn [e]
                                (when @center
                                  (let [p (get-point e)
                                        [vx vy] (mapv - p @center)
                                        r (Math/sqrt (+ (* vx vx) (* vy vy)))]
                                    (rf/dispatch [::set-radius r]))))
                :onMouseUp    #(rf/dispatch [::add-circle @center @radius])
                :onMouseLeave (fn [_]
                                (rf/dispatch [::set-center nil])
                                (rf/dispatch [::set-radius nil]))}]
        (doall
          (for [[index circle] (map-indexed (fn [i c] [i c]) @circles)
                :let [[cx cy] (:center circle)
                      radius (if (= index @edit-index)
                               @edit-radius
                               (:radius circle))]]
            ^{:key (assoc circle :index index)}
            [:circle (cond-> {:cx     cx :cy cy :r radius
                              :stroke :black :fill :green}
                             (not= index @edit-index)
                             (assoc :on-click #(rf/dispatch [::begin-editing index radius]))
                             ;; No pointer/mouse events on drawn circles when drawing a new one.
                             @center
                             (assoc :pointer-events :none))]))
        [draw-active-circle]]
       (if @edit-index
         [radius-editor]
         [undo-redo-buttons])
       gc06/about])))