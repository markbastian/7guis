(ns guis.gui07
  (:require [reagent.core :as r]
            [instaparse.core :as insta]))

(def parser
  (insta/parser
    "S = FORM | FORMULA | VALUE | CELL
    (* PRIMITIVE TYPES *)
    <STRING_ESCAPE> = '\\''
    STRING = <STRING_ESCAPE> #'[^\\']*' <STRING_ESCAPE>
    INT = #'-?[0-9]+'
    FLT = #'-?[0-9]+\\.[0-9]+'
    VALUE = STRING | INT | FLT

    <ROW> = #'[A-Z]'
    <COL> = #'[0-9]'
    CELL = ROW COL
    FN = '+' | '-'
    <WS> = <' '>
    <ARG> = FORM | CELL | VALUE
    <ARGS> = ARG (WS+ ARG)*
    FORM = <'('> FN WS+ ARGS <')'>
    FORMULA = <'='> FORM
    "))

(defn pre-transform [parse-tree]
  (insta/transform
    {:INT     (fn [s] (js/parseInt s))
     :FLT     (fn [s] (js/parseFloat s))
     :STRING  identity
     :FN      (fn [op] [:FN ({"+" + "-" -} op)])
     :CELL    (fn [r c] (let [cell (keyword (str r c))]
                          (with-meta [:CELL cell] {:deps #{cell}})))
     :FORM    (fn [op & r]
                (let [m (reduce into (map (comp :deps meta) r))]
                  (with-meta (into [:FORM op] r) {:deps (set m)})))
     :FORMULA identity
     :S       identity}
    parse-tree))

;TODO - Check graph for extant ancestors/descendants and wipe them.
(defn create-cell [graph cell text]
  (let [parse-tree (-> text parser pre-transform)
        ancestors (:deps (meta parse-tree))
        c {:text       text
           :parse-tree parse-tree
           :ancestors  ancestors}]
    (reduce
      (fn [graph ancestor]
        (update-in graph [ancestor :descendants]
                   (fn [descendants] (set (conj descendants cell)))))
      (assoc graph cell c) ancestors)))

(defn post-transform [parse-tree m]
  (insta/transform
    {:VALUE identity
     :FN    identity
     :CELL  (fn [cell] (m cell))
     :FORM  (fn [op & args] (apply op args))}
    parse-tree))

(defn eval-cell [graph cell]
  (let [{:keys [parse-tree]} (graph cell)]
    (post-transform parse-tree graph)))

(-> "A1" parser pre-transform)
(-> "42" parser pre-transform)
(-> "-21" parser pre-transform)
(-> "3.14" parser pre-transform)
(-> "-3.14" parser pre-transform)
(-> "'-3.14'" parser pre-transform)
(-> "=(+ 5 (- A4 7))" parser pre-transform)
(-> "=(- A4 7)" parser pre-transform meta)
(-> "=(+ 5 (- A4 7))" parser pre-transform)
(-> "=(+ 5 B5 (- A4 7) (- C1 7))" parser pre-transform meta)
(-> "=(+ 5 (- A4 7))" parser pre-transform (post-transform {:A4 42}))
(-> "=(+ 5 (- 7))" parser pre-transform (post-transform {}))

(def state (r/atom
             (-> {}
                 (create-cell :A1 "42")
                 (create-cell :B2 "=(+ A1 1)"))))

(defn render []
  (let []
    (fn []
      (let [nrows 10 ncols 10]
        [:div
         [:h2 "Task 7: Cells"]
         [:table {:style {:width "100%" :border "1px solid black"}}
          [:thead
           [:tr {:style {:border "1px solid black"}}
            (doall
              (for [i (range (inc ncols))
                    :let [col (char (+ 64 i))]]
                ^{:key (str "col:" col)}
                [:th {:style {:border "1px solid black"}}
                 (when-not (zero? i) col)]))]]
          [:tbody
           (doall
             (for [row-index (map inc (range nrows))]
               ^{:key (str "data col" row-index)}
               [:tr
                (doall
                  (for [column-index (range (inc ncols))
                        :let [col (char (+ 65 column-index))
                              cell-index (str col row-index)]]
                    ^{:key (str "cell:" cell-index)}
                    [:td {:style {:border "1px solid black"}}
                     (if (zero? column-index)
                       row-index
                       [:input.form-control
                        {:type      "text"
                         :value     (get @state (keyword cell-index))
                         :on-change (fn [e]
                                      (let [v (.-value (.-target e))]
                                        (swap! state assoc (keyword cell-index) v)))}])]))]))]]]))))
