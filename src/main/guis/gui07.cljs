(ns guis.gui07
  (:require [reagent.core :as r]
            [xcell.core :as xcell]))

(def state (-> (r/atom {})
               (xcell/update-cell! :A1 "42")
               (xcell/update-cell! :B2 "=(+ A1 1)")))

(defn enter-handler [e cell]
  (let [v (.-value (.-target e))]
    (when (= (.-key e) "Enter")
      (doto state
        (xcell/update-cell! cell v)
        (swap! dissoc :temp-value)))))

(defn render-table-header [border-style ncols]
  [:thead
   [:tr (assoc-in border-style [:style :background-color] :gray)
    (doall
      (for [i (range (inc ncols)) :let [col (char (+ 64 i))]]
        ^{:key (str "col:" col)}
        [:th (assoc-in border-style [:style :text-align] :center)
         (when-not (zero? i) col)]))]])

(defn render-table-body [state nrows ncols]
  [:tbody
   (doall
     (for [row-index (map inc (range nrows))]
       ^{:key (str "data col" row-index)}
       [:tr
        (doall
          (for [column-index (range (inc ncols))
                :let [col (char (+ 64 column-index))
                      cell-index (str col row-index)
                      cell (keyword cell-index)]]
            ^{:key (str "cell:" cell-index)}
            [:td (cond-> {:style {:border "1px solid black"}}
                         (zero? column-index)
                         (-> (assoc-in [:style :text-align] :center)
                             (assoc-in [:style :background-color] :gray)))
             (if (zero? column-index)
               row-index
               [:input.form-control
                {:type       "text"
                 :style      {:border :none :width :120px}
                 :value      (let [{:keys [temp-value active-cell] :as s} @state]
                               (or
                                 (and (= cell active-cell) temp-value)
                                 (get-in s [cell :value])))
                 :on-click   (fn [_]
                               (let [{:keys [active-cell temp-value]} @state]
                                 (when
                                   (and active-cell temp-value)
                                   (xcell/update-cell! state active-cell temp-value))
                                 (doto state
                                   (swap! assoc
                                          :active-cell cell
                                          :temp-value (get-in @state [cell :text])))))
                 :on-change  (fn [e]
                               (let [v (.-value (.-target e))]
                                 (swap! state assoc :temp-value v)))
                 :onKeyPress (fn [e] (enter-handler e cell))}])]))]))])

(defn render []
  (let []
    (fn []
      (let [nrows 100
            ncols 26
            border-style {:style {:border "1px solid black"}}]
        [:div
         [:h2 "Task 7: Cells"]
         [:div {:style {:width :100% :height :600px :overflow-x :auto}}
          [:table border-style
           [render-table-header border-style ncols]
           [render-table-body state nrows ncols]]]
         (when-some [active-cell (:active-cell @state)]
           [:div.input-group-prepend
            [:span#basic-addon1.input-group-text (str (name active-cell) ":")]
            [:input.form-control
             {:type       "text"
              :value      (:temp-value @state)
              :on-change  (fn [e]
                            (let [v (.-value (.-target e))]
                              (swap! state assoc :temp-value v)))
              :onKeyPress (fn [e] (enter-handler e active-cell))}]])
         [:h5 "About"]
         [:p "This is an Excel-like spreadsheet with formulas, propagation, etc. Here are some things to try:"]
         [:ul
          [:li "Click a cell. Note that you can edit the cell in place or in the formula bar at the bottom."]
          [:li "Formulas are Clojure-like. Try the following:"
           [:ul
            [:li "Enter a value like 42, 3.14159"]
            [:li "Enter a formula starting with an equal sign followed by a form, like =(+ A1 4)"]
            [:li "Forms can be nested (e.g. =(+ A1 (/ B3 1)))"]]]
          [:li "Current valid operators are +, -, *, /, and sqrt."]
          [:li "NOTE: Cycle detection hasn't been added yet, so currently a non-DAG will break. Future options:"
           [:ul
            [:li "Catch cycles and don't allow."]
            [:li "Allow for fixed-point iteration. The numerical analyst in me wants this."]]]]]))))
