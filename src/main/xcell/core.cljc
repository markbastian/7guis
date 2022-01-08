(ns xcell.core
  (:require [xcell.parser :as parser]
            [instaparse.core :as insta]))

(defn eval-cell
  "Evaluate in place the contents of the given cell."
  ;TODO - Barf if there is a failure.
  [graph cell]
  (let [{:keys [parse-tree]} (graph cell)
        new-value (parser/post-transform parse-tree graph)]
    (assoc-in graph [cell :value] new-value)))

(defn remove-ancestor-links
  "Remove links to this cell from its ancestors the graph."
  [graph cell]
  (let [ancestors (get-in graph [cell :ancestors])]
    (reduce
      (fn [g ancestor] (update-in g [ancestor :descendants] disj cell))
      graph ancestors)))

(defn add-ancestor-links
  "Add links to this cell from its ancestors the graph."
  [graph cell ancestors]
  (reduce
    (fn [g ancestor]
      (update-in g [ancestor :descendants]
                 (fn [descendants] (set (conj descendants cell)))))
    graph ancestors))

(defn update-cell
  "Update the string contents of a cell."
  [graph cell text]
  (if (seq text)
    (let [p (parser/parser text)]
      (if-not (insta/failure? p)
        (let [parse-tree (parser/pre-transform p)
              ancestors (:deps (meta parse-tree))
              value (parser/post-transform parse-tree graph)
              new-cell {:text       text
                        :parse-tree parse-tree
                        :ancestors  (set ancestors)
                        :value      value}]
          (-> graph
              (remove-ancestor-links cell)
              (update cell (fn [c]
                             (dissoc (into (or c {}) new-cell) :error)))
              (add-ancestor-links cell ancestors)))
        (-> graph
            (assoc-in [cell :text] text)
            (assoc-in [cell :error] (insta/get-failure p)))))
    graph))

(defn propagate-change
  "Propagate the value of a cell to its children."
  [graph cell]
  (let [{:keys [descendants]} (graph cell)]
    (reduce
      (fn [graph descendant] (eval-cell graph descendant))
      graph descendants)))

(defn update-cell!
  "Update the value of a cell."
  [state cell text]
  (doto state
    (add-watch
      cell
      (fn [_ state o n]
        (let [{descendants :descendants} (n cell)]
          (when-not (= (get o cell) (get n cell))
            (doseq [descendant descendants]
              (swap! state eval-cell descendant))))))
    (swap! update-cell cell text)))

(comment
  (update-cell {} :A1 "42")
  ;TODO - Decide on the correct behavior here. Currently does nothing.
  ; Should it eject the cell from the state?
  (update-cell {} :A1 "")

  (-> {}
      (update-cell :A1 "42")
      (update-cell :A2 "55")
      (update-cell :B2 "=(+ A1 42)")
      (update-cell :B2 "=(+ A2 45)")
      (update-cell :A2 "100")
      (propagate-change :A2))

  (-> {}
      (update-cell :A1 "42")
      (update-cell :B2 "=(+ A1 42)")
      (update-cell :C3 "=(+ B2 42)")
      (update-cell :D4 "=(+ B2 C3)"))

  (def gstate
    (-> (atom {})
        (update-cell! :A1 "42")
        (update-cell! :B2 "=(+ A1 42)")
        (update-cell! :C3 "=(+ B2 42)")
        (update-cell! :D4 "=(+ B2 C3)")))

  @gstate
  (update-cell! gstate :A1 "0")

  (update-cell! gstate :A1 "")
  )


