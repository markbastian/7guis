# 7 GUIS
An implementation of the [7 Tasks](https://eugenkiss.github.io/7guis/tasks).

## Status
MVP Feature Complete.

## TODOS

* There's a string formatting issue with the temp converter in which you can't start out with a negative temp or type a decimal in place. The problem has to do with how the parsing of the string occurs (not allowing the intermediate "bad" string).
* I am not a CSS genius. I need to skill up on that front and add a little polish:
  * Make cells in table fill entire width of screen.
  * Add horizontal and vertical scroll bars.

## Tips

* Ensure that the `shadow-cljs` alias is active.
* `clj -A:shadow-cljs compile frontend` will compile the app.
* `clj -A:shadow-cljs watch frontend` will launch the build system.
* Once the watch is running:
  * Jack in to localhost:7888
  * Open public/index.html in your browser
  * Execute `(shadow.cljs.devtools.api/nrepl-select :frontend)` in your ns.