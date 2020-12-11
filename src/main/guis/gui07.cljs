(ns guis.gui07
  (:require [reagent.core :as r]
            [xcell.core :as xcell]))

(def state (-> (r/atom {})
               (xcell/update-cell! :A1 "42")
               (xcell/update-cell! :B2 "=(+ A1 1)")))

;TODO - Get horizontal scrollbars working right. Need to level up my css-fu.
; https://stackoverflow.com/questions/5533636/add-horizontal-scrollbar-to-html-table
; https://www.w3schools.com/howto/howto_css_menu_horizontal_scroll.asp
(defn render []
  (let []
    (fn []
      (let [nrows 10 ncols 5]
        [:div
         [:h2 "Task 7: Cells"]
         [:div
          [:table {:style {:border "1px solid black"}}
           [:thead
            [:tr {:style {:border "1px solid black"}}
             (doall
               (for [i (range (inc ncols))
                     :let [col (char (+ 64 i))]]
                 ^{:key (str "col:" col)}
                 [:th {:style {:border "1px solid black" :width :100px}}
                  (when-not (zero? i) col)]))]]
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
                     ;http://help.openspan.com/80/HTML_Table_Designer/html_table_designer_-_Cell_-_properties,_methods_and_events.htm
                     [:td {:style {:border "1px solid black" :width :100px}}
                      (if (zero? column-index)
                        row-index
                        [:input.form-control
                         {:type       "text"
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
                          :onKeyPress (fn [e]
                                        (let [v (.-value (.-target e))]
                                          (when (= (.-key e) "Enter")
                                            (doto state
                                              (xcell/update-cell! cell v)
                                              (swap! dissoc :temp-value)))))}])]))]))]]]
         (when-some [active-cell (:active-cell @state)]
           [:div.input-group-prepend
            [:span#basic-addon1.input-group-text (str (name active-cell) ":")]
            [:input.form-control
             {:type "text"
              :value     (let [{:keys [temp-value] :as s} @state]
                           temp-value)
              :on-change  (fn [e]
                            (let [v (.-value (.-target e))]
                              (swap! state assoc :temp-value v)))
              :onKeyPress (fn [e]
                            (let [v (.-value (.-target e))]
                              (when (= (.-key e) "Enter")
                                (doto state
                                  (xcell/update-cell! active-cell v)
                                  (swap! dissoc :temp-value)))))
              }]])]))))
