(ns xcell.parser
  (:require [instaparse.core :as insta]))

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
    FN = '+' | '-' | '*' | '/' | 'sqrt'
    <WS> = <' '>
    <ARG> = FORM | CELL | VALUE
    <ARGS> = ARG (WS+ ARG)*
    FORM = <'('> FN WS+ ARGS <')'>
    FORMULA = <'='> WS* (FORM | CELL)
    "))

(defn pre-transform [parse-tree]
  (insta/transform
    {:INT     (fn [s] (js/parseInt s))
     :FLT     (fn [s] (js/parseFloat s))
     :STRING  identity
     :FN      (fn [op] [:FN ({"+" + "-" - "*" * "/" / "sqrt" Math/sqrt} op)])
     :CELL    (fn [r c] (let [cell (keyword (str r c))]
                          (with-meta [:CELL cell] {:deps #{cell}})))
     :FORM    (fn [op & r]
                (let [m (reduce into (map (comp :deps meta) r))]
                  (with-meta (into [:FORM op] r) {:deps (set m)})))
     :FORMULA identity
     :S       identity}
    parse-tree))

(defn post-transform [parse-tree m]
  (insta/transform
    {:VALUE identity
     :FN    identity
     :CELL  (fn [cell] (get-in m [cell :value]))
     :FORM  (fn [op & args] (apply op args))}
    parse-tree))

(def text->parse-tree (comp pre-transform parser))

(comment
  ;TODO - Create tests
  (text->parse-tree "A1")
  (text->parse-tree "=A1")
  (text->parse-tree "42")
  (text->parse-tree "-21")
  (text->parse-tree "3.14")
  (text->parse-tree "-3.14")
  (text->parse-tree "'-3.14'")
  (text->parse-tree "=(+ 5 (- A4 7))")
  (-> "=(- A4 7)" text->parse-tree meta :deps)
  (-> "=  (- A4 7)" text->parse-tree meta :deps)
  (text->parse-tree "=(+ 5 (- A4 7))")
  (-> "=(+ 5 B5 (- A4 7) (- C1 7))" text->parse-tree meta)
  (-> "=(+ 5 (- A4 7))" text->parse-tree (post-transform {:A4 42}))
  (-> "=(+ 5 (- 7))" text->parse-tree (post-transform {}))
  (-> "=(sqrt 2)" text->parse-tree (post-transform {})))