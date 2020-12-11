# 7 GUIS
An implementation of the [7 Tasks](https://eugenkiss.github.io/7guis/tasks).

This is the code repo. See it live [right here](https://markbastian.github.io/7guis/public/index.html).

## Status
MVP Feature Complete.

## Key Concepts

Each of UIs has a standalone state (a reagent atom) that is the source-of-record truth model for the UI. This is critical so you don't have state spread out all over the place. Get the model right and sync the UI to it.

The Cells API is fairly cool. I broke it out as two cljc nses so it could potentially be front-end, back-end, Swing, etc. The parser is a bit limited, but this is round 0. Another thought is to have self-hosted cljs so that I can do "real" evaluation of forms. I'd use a similar strategy for cell lineage and propagation.

## TODOS

* Cells
  * Add cycles or cycle limits
  * Self-hosted cljs for cells vs. a parser?
  * More functions?
  * Evaluate watch-per-cell vs. global watch for performance
  * Format column header cells and row index cells to gray and center aligned
* Testing
  * Move rich comment blocks into tests.

## Tips

* Ensure that the `shadow-cljs` alias is active.
* `clj -A:shadow-cljs compile frontend` will compile the app.
* `clj -A:shadow-cljs watch frontend` will launch the build system.
* Once the watch is running:
  * Jack in to localhost:7888
  * Open public/index.html in your browser
  * Execute `(shadow.cljs.devtools.api/nrepl-select :frontend)` in your ns.
* `clj -A:shadow-cljs release frontend` will create a minified js file.
* Refresher on [gh-pages](https://jiafulow.github.io/blog/2020/07/09/create-gh-pages-branch-in-existing-repo/) creation.