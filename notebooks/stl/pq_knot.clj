^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.pq-knot
  #:nextjournal.clerk{:toc true}
  (:refer-clojure
   :exclude [+ - * / zero? compare divide numerator denominator
             infinite? abs ref partial =])
  (:require [emmy.clerk :as ec]
            [emmy.env :as e :refer :all]
            [emmy.leva :as leva]
            [emmy.mathbox.plot :as plot]
            [emmy.viewer :as ev]
            [nextjournal.clerk :as clerk]))

;; ## (p, q) Torus Knot
;;
;; A (p,q) torus knot is a loop of string that winds `q` times through the hole
;; of a donut, and revolves `p` full times around the axis of revolution, before
;; joining up again.
;;
;; Let's play!

{::clerk/width :wide}

^{::clerk/visibility {:code :hide :result :hide}}
(ec/install!)

;; Returns a function of `theta` that produces a 3-vector of the XYZ coordinates
;; of a `(p, q)` torus knot wrapped around a torus (donut) with major radius `R`
;; and minor radius `r`.

(defn torus-knot [R r p q]
  (fn [theta]
    (let [xr (+ R (* r (cos (* q theta))))]
      [(* xr (cos (* p theta)))
       (* xr (sin (* p theta)))
       (* r  (sin (* q theta)))])))

;; Let's take a look:

(clerk/with-viewer ec/multiviewer
  ((torus-knot 'R 'r 'p 'q) 'theta))

(defn toroidal->rect [R r]
  (fn [[theta phi]]
    (* (rotate-z-matrix phi)
       (up (+ R (* r (cos theta)))
           0
           (* r (sin theta))))))

;; Here is a visualization of the torus knot wrapped around a torus:

^{:nextjournal.clerk/visibility {:code :fold}}
(ev/with-let [!opts {:R 2 :r 0.5 :p 7 :q 8}]
  (plot/scene
   (leva/controls
    {:atom !opts
     :folder {:name "Torus and Curve"}
     :schema
     {:R {:min 0.5 :max 2 :step 0.01}
      :r {:min 0.5 :max 2 :step 0.01}
      :p {:min 0 :max 32 :step 1}
      :q {:min 0 :max 32 :step 1}}})

   (plot/parametric-curve
    {:f (ev/with-params {:atom !opts :params [:R :r :p :q]}
          torus-knot)
     :t [(- Math/PI) Math/PI]
     :samples 512})

   (plot/parametric-surface
    {:f (ev/with-params {:atom !opts :params [:R :r]}
          toroidal->rect)
     :u [(- Math/PI) Math/PI]
     :v [(- Math/PI) Math/PI]})))

;; Given a parametric function `f` of a single variable `t`, generates a
;; function of `t` that returns a matrix with columns `B`, `N`, `T` of the
;; Frenet-Serret frame at point `(f t)`.

;; See the section on 'other
;; expressions' [here](https://en.wikipedia.org/wiki/Frenet%E2%80%93Serret_formulas#Other_expressions_of_the_frame).

(defn ->TNB [f]
  (let [make-unit (fn [v] (/ v (abs v)))
        T         (fn [theta]
                    (make-unit ((D f) theta)))
        N         (fn [theta]
                    (make-unit ((D T) theta)))]
    (fn [t]
      (let [T-t (T t)
            N-t (N t)
            B-t (cross-product T-t N-t)]
        (matrix-by-cols B-t N-t T-t)))))

;; Given some radius `r` and `angle`, returns the x-y-z coordinates of a point
;; at angle `theta` on the unit circle sitting flat in the x-y plane.

(defn circle [r angle]
  [(* r (cos angle))
   (* r (sin angle))
   0])

(clerk/with-viewer ec/multiviewer
  (circle 'r 'theta))

(clerk/with-viewer ec/multiviewer
  ((->TNB (fn [angle] (circle 'r angle)))
   't))

;; Given:

;; - `R`    - the major radius of a torus
;; - `r2`   - minor radius of a torus
;; - `r3`   - radius of a helitorus cross-section
;; - `p, q` - torus knot params

;; Returns a function that generates the x-y-z coordinates of a point on a `(p,
;; q)` torus knot at angle `theta` around the torus and `phi` around a tube
;; wrapping the curve.

(defn path->tube [theta->xyz r]
  (let [M  (->TNB theta->xyz)]
    (fn [[theta phi]]
      (+ (theta->xyz theta)
         (* (M theta)
            (circle r phi))))))

(defn torus-knot-tube [R r2 r3 p q]
  (path->tube
   (torus-knot R (+ r2 r3) p q)
   r3))

;; Let's take a look:

^{:nextjournal.clerk/width :full
  :nextjournal.clerk/visibility {:code :fold}}
(ev/with-let [!opts {:p 7 :q 8 :r1 1.791 :r2 0.95 :r3 0.1 :torus? false}]
  (plot/scene
   {:threestrap {:controls {:klass :trackball}}
    :container {:style {:height "500px" :width "100%"}}
    :camera [1 3 1]
    :range [[-1 1] [-1 1] [-1 1]]
    :axes []
    :grids []}

   (leva/controls
    {:folder {:name "PQ Knot"}
     :atom !opts
     :schema
     {:p {:min 0 :max 32 :step 1}
      :q {:min 0 :max 32 :step 1}
      :r1 {:min 0 :max 3 :step 0.001}
      :r2 {:min 0.0 :max 2.5 :step 0.01}
      :r3 {:min 0.0 :max 0.2 :step 0.01}}})

   (plot/parametric-surface
    {:f (ev/with-params
          {:atom !opts :params [:r1 :r2 :r3 :p :q]}
          torus-knot-tube)
     :simplify? false
     :color 0xcc0040
     :opacity 1
     :u-samples 512
     :v-samples 16
     :grid-color 0xffffff
     :grid-opacity 1
     :grid-width 3
     :grid-u 100
     :grid-v 4
     :u [(- Math/PI) Math/PI]
     :v [(- Math/PI) Math/PI]})
   (list 'when (list :torus? (list 'deref !opts))
         (plot/parametric-surface
          {:f (ev/with-params {:atom !opts :params [:r1 :r2 :r3]}
                (fn [r1 r2 r3]
                  (toroidal->rect r1 (+ r2 r3))))
           :u [(- Math/PI) Math/PI]
           :v [(- Math/PI) Math/PI]}))))
