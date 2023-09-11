^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.scratch
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / zero? compare divide numerator denominator
             infinite? abs ref partial =])
  (:require [emmy.env :as e :refer :all]))

;; # Generics and Calculus


(def X (literal-up 'x 2))
(def Y (literal-up 'y 2))

(->infix
 (simplify
  (- (dot-product X Y)
     (* (abs X)
        (abs Y)))))
