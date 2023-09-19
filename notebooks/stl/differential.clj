^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.differential
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / zero? compare divide numerator denominator
             infinite? abs ref partial =])
  (:require [emmy.clerk :as ec]
            [emmy.differential]
            [emmy.env :refer :all]
            [nextjournal.clerk :as clerk]))

;; # Generics and Calculus

;; This function returns a dual number, $x+\varepsilon$.

(defn bundle-element [x]
  (emmy.differential/bundle-element x 0))

(def diff-viewer
  {:pred emmy.differential/differential?
   :transform-fn
   (clerk/update-val
    (fn [d]
      (ec/->TeX
       (let [x  (emmy.differential/primal-part d)
             dx (emmy.differential/extract-tangent d 0)]
         (list '+ x (list '* dx 'epsilon))))))})

;; Let's install a Clerk "viewer" that will render dual numbers using $\TeX$:

^{::clerk/visibility {:result :hide}}
(ec/install! diff-viewer)

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
