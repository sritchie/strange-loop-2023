^#:nextjournal.clerk
{:toc true
 :visibility :hide-ns}
(ns stl.pq-knot
  (:refer-clojure
   :exclude [+ - * / zero? compare divide numerator denominator
             infinite? abs ref partial =])
  (:require [stl.viewers :refer [multiviewer]]
            [emmy.env :as e :refer :all]
            #_[emmy.expression.compile :as xc]
            [nextjournal.clerk :as clerk]))

;; ## (p, q) Torus Knot

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

^{::clerk/viewer multiviewer}
((torus-knot 'R 'r 'p 'q) 'theta)

;; Given a parametric function `f` of a single variable `t`, generates a
;; function of `t` that returns a matrix with columns `B`, `N`, `T` of the
;; Frenet-Serret frame at point `(f t)`.

;; See the section on 'other
;; expressions' [here](https://en.wikipedia.org/wiki/Frenet%E2%80%93Serret_formulas#Other_expressions_of_the_frame).

(defn ->TNB [f]
  (letfn [(make-unit [v]
            (/ v (abs v)))
          (T [theta]
            (make-unit ((D f) theta)))
          (N [theta]
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

^{::clerk/viewer multiviewer}
(circle 'r 'theta)

^{::clerk/viewer multiviewer}
(simplify
 ((->TNB (partial circle 'r))
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

#_
(-> (xc/compile-state-fn
     torus-knot-tube
     ['R 'r_2 'r_3 'p 'q]
     ['theta 'phi]
     {:mode :js
      :simplify? false
      :calling-convention :primitive
      :generic-params? true})
    (nth 3)
    (println))

;; - [Trefoil knot](https://mathworld.wolfram.com/TrefoilKnot.html): (3, 2)
;; - [Solomon's Seal knot](https://mathworld.wolfram.com/SolomonsSealKnot.html): (5, 2)
;; ## Animation

;; TODO get this thing working too.
