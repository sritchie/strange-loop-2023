^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.taylor-series
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial
             infinite? abs])
  (:require [nextjournal.clerk :as clerk]
            [emmy.clerk :as ec]
            [emmy.calculus.derivative]
            [emmy.env :as e :refer :all]
            [emmy.expression.compile :as xc]
            [emmy.series]))

;; ## Taylor Series Visualizations

^{::clerk/visibility {:code :hide :result :hide}}
(ec/install!)

^{::clerk/visibility {:code :hide :result :hide}}

(defn series-transform [f n mode]
  (mapv
   (fn [n]
     (xc/compile-fn
      (fn [x]
        (emmy.series/sum (f x) n)) 1 {:mode mode :cache? true}))
   (range n)))

(ec/->TeX
 (((exp D) (literal-function 'f)) 'x))

;; Let's try it:

^{::clerk/viewer ec/multiviewer}
(-> (emmy.series/sin-series 'x)
    (series:sum 10))

;; Honestly, I'm not really sure what this is going to look like. Let's get it
;; onto a plot.

^{::clerk/visibility {:code :hide :result :hide}}
(def series-viewer
  {:transform-fn
   (comp clerk/mark-presented
         (clerk/update-val
          (fn [f]
            (let [opts   (or (meta series) {})
                  n      (:n opts 5)
                  series (emmy.calculus.derivative/taylor-series f)]
              (merge opts
                     {:f (xc/compile-fn f 1 {:mode :js})
                      :fns (series-transform series (inc (* 2 n)) :js)
                      :tex (let [fx (series 'x)]
                             (mapv (fn [n]
                                     (->TeX
                                      (simplify
                                       (series:sum fx n))))
                                   (range (inc (* 2 n)))))
                      :params {:n n}
                      :schema {:n {:min 0 :max (* 2 n) :step 1}}})))))
   :render-fn
   '(fn [{f :f
         fns :fns
         tex :tex
         params :params
         schema :schema
         cart-opts :cartesian
         plot-opts :plot
         :or {cart-opts {}
              plot-opts {}}}]
      (reagent.core/with-let
        [!params (reagent.core/atom params)
         f'      (apply js/Function f)
         fns'    (mapv #(apply js/Function %) fns)
         colors  (cycle
                  ["var(--mafs-red)"
                   "var(--mafs-orange)"
                   "var(--mafs-green)"
                   "var(--mafs-blue)"
                   "var(--mafs-indigo)"
                   "var(--mafs-violet)"
                   "var(--mafs-pink)"
                   "var(--mafs-yellow)"])]
        [:<>
         [leva.core/Controls
          {:atom !params :schema schema}]
         [nextjournal.clerk.render/inspect
          (nextjournal.clerk.viewer/tex
           (nth tex (:n @!params)))]
         (into [mafs.core/Mafs {:zoom {:min 0.1 :max 2}}
                [mafs.coordinates/Cartesian cart-opts]
                [mafs.plot/OfX {:y f'}]]
               (map-indexed
                (fn [i color]
                  ^{:key i}
                  [mafs.plot/OfX {:y (nth fns' i)
                                  :color color}])
                (take (:n @!params) colors)))]))})

;; ### Demo
;;
;; Let's check out the taylor series expansions around 0 for a few different
;; functions.

^{::clerk/width :wide
  ::clerk/viewer series-viewer}
sin

^{::clerk/width :wide
  ::clerk/viewer series-viewer}
exp

^{::clerk/width :wide
  ::clerk/viewer series-viewer}
(comp log #(+ % 1))


;; ## Mafs!

^{::clerk/viewer cv/parametric-viewer
  ::clerk/width :full}
{:t [0 (* 2 pi)]
 :f [cos sin]}

^{::clerk/viewer cv/parametric-viewer
  ::clerk/width :full}
{:t [0 (* 2 pi)]
 :f [cos (comp sin (partial * 3))]}
