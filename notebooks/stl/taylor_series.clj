^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.taylor-series
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial
             infinite? abs])
  (:require [emmy.clerk :as ec]
            [emmy.calculus.derivative]
            [emmy.env :refer :all]
            [emmy.mafs :as mafs]
            [emmy.series]
            [nextjournal.clerk :as clerk]))

;; ## Taylor Series Visualizations
;;
;; This namespace contains visualizations of the taylor series expansion of...
;; well, of any function you want to pass in!

^{::clerk/visibility {:code :hide :result :hide}}
(ec/install!)

(ec/->TeX
 (((exp D) (literal-function 'f)) 'x))

;; Let's try it:

^{::clerk/viewer ec/multiviewer}
(-> (emmy.series/sin-series 'x)
    (series:sum 10))

;; Honestly, I'm not really sure what this is going to look like. Let's get it
;; onto a plot.

(defn visualize [f n]
  (let [series (emmy.calculus.derivative/taylor-series f)
        colors (cycle [:red :orange :green :blue :indigo :violet :pink :yellow])]
    (clerk/col
     (ec/->TeX
      (simplify
       (series:sum (series 'x) n)))
     (apply mafs/mafs {:zoom {:min 0.1 :max 2}}
            (mafs/cartesian)
            (mafs/of-x {:y f})
            (map-indexed
             (fn [n color]
               (mafs/of-x {:y #(emmy.series/sum (series %) (inc n))
                           :color color}))
             (take n colors))))))

;; ### Demo
;;
;; Let's check out the taylor series expansions around 0 for a few different
;; functions.

{::clerk/width :wide}

(visualize sin 5)
(visualize exp 5)
(visualize (comp log #(+ % 1)) 5)
