# 7 GUIS
An implementation of the [7 Tasks](https://eugenkiss.github.io/7guis/tasks).

## Tips

* Ensure that the `shadow-cljs` alias is active.
* `clj -A:shadow-cljs compile frontend` will compile the app.
* `clj -A:shadow-cljs watch frontend` will launch the build system.
* Once the watch is running:
  * Jack in to localhost:7888
  * Open public/index.html in your browser
  * Execute `(shadow.cljs.devtools.api/nrepl-select :frontend)` in your ns.