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
      (let [nrows 100 ncols 10]
        [:div
         [:h2 "Task 7: Cells"]
         [:div {:style {:width :800px
                        :height :600px
                        :display :block
                        :overflow :auto
                        :overflow-x :auto}}
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
                     [:td {:style {:border "1px solid black" :width :100px}}
                      (if (zero? column-index)
                        row-index
                        [:input.form-control
                         {:type       "text"
                          :value      (get-in @state [cell :value])
                          :on-click   (fn [_] (swap! state assoc :active-cell cell))
                          :on-change  (fn [e]
                                        (let [v (.-value (.-target e))]
                                          (xcell/update-cell! state cell v)))
                          :onKeyPress (fn [e]
                                        (if (= (.-key e) "Enter")
                                          (.log js/console "Enter")
                                          (.log js/console "Not Enter")))
                          ;:on-keyup (fn [e] (js/alert "faess"))
                          }])]))]))]]]
         (when-some [active-cell (:active-cell @state)]
           [:div.input-group-prepend {:style {:width :800px}}
            [:span#basic-addon1.input-group-text (str (name active-cell) ":")]
            [:input.form-control
             {:type "text"
              ;:value     (some-> @temperature-state c->f)
              ;:on-change (fn [e] (update-farenheit
              ;                     temperature-state
              ;                     (.-value (.-target e))))
              }]])]))))
