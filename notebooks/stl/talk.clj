^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.talk
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / zero? compare divide numerator denominator
             infinite? abs ref partial =])
  (:require [stl.viewers :as cv]
            [emmy.clerk :as ec]
            [emmy.differential]
            [emmy.env :refer :all]
            [nextjournal.clerk :as clerk]))

;; # Generics and Calculus

;; This function returns a dual number, $x+\varepsilon$.

(defn bundle-element [x]
  (emmy.differential/bundle-element x 0))

;; Let's install a Clerk "viewer" that will render dual numbers using $\TeX$:

^{::clerk/visibility {:result :hide}}
(ec/install! cv/diff-viewer)

;; ## Autodiff with Emmy!

(def epsilon
  (bundle-element 0))

(+ 'x epsilon)

(sin (+ 'x epsilon))

(expt (+ 'x epsilon) 4)

;; In Emmy, the `D` operator applied to a function takes derivatives.

((D sin) 'x)

(((square D) sin) 'x)

;; How bananas is this??

(ec/->TeX
 (((exp D) (literal-function 'f)) 'x))

;; Next, head over to `taylor_series.clj`.
