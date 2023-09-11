(ns stl.toroid-sim
  (:require [conj.toroid :as t]
            [nextjournal.clerk :as clerk]))

;; ## Extending Toroid


^{::clerk/viewer
  (assoc t/geodesic-viewer :render-fn 'demo.mathbox/ToroidPoint)}
(let [R 2
      r 0.5]
  {:params {:R R :r r}
   :schema
   {:R   {:min 0.5 :max 2 :step 0.01}
    :r   {:min 0.5 :max 2 :step 0.01}}
   :keys [:R :r]
   :state->xyz t/toroidal->rect
   :L t/L-toroidal
   :initial-state [0 [0 0] [6 1]]
   :cartesian
   {:range [[-10 10]
            [-10 10]
            [-10 10]]
    :scale [3 3 3]}})
