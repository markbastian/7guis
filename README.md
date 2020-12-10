# 7 GUIS
An implementation of the [7 Tasks](https://eugenkiss.github.io/7guis/tasks).

## Status
6.5 complete.

## TODOS

* There's a string formatting issue with the temp converter in which you can't start out with a negative temp or type a decimal in place. The problem has to do with how the parsing of the string occurs (not allowing the intermediate "bad" string).
* I just got started on 7, so it is 1/2 way done.
  * The plan is to write a parser that handles s-expression based formulas. This parser is pretty much ready subject to additional functions.
  * I should probably replace with self-hosted cljs, but first any cell-like symbol needs to be replaced with a cell reference.
  * Complete the call graph. I plan to tag all cells such that they know their direct descendants. This way any time a cell changes, we can propagate that to its descendants. If the graph is a DAG we can terminate. I am planning on adding cycles which will settle after a number of iterations or numerical stability.
  * Note that this portion should be ported to cljc as it makes a common backend that is UI agnostic and could just as easily be front end, back end, web, swing, etc.

## Tips

* Ensure that the `shadow-cljs` alias is active.
* `clj -A:shadow-cljs compile frontend` will compile the app.
* `clj -A:shadow-cljs watch frontend` will launch the build system.
* Once the watch is running:
  * Jack in to localhost:7888
  * Open public/index.html in your browser
  * Execute `(shadow.cljs.devtools.api/nrepl-select :frontend)` in your ns.