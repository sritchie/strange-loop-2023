^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.phase-portrait
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / = zero? compare abs
             numerator denominator ref partial infinite?])
  (:require [emmy.clerk :as ec]
            [emmy.env :refer :all]
            [emmy.leva :as leva]
            [emmy.mathbox :as box]
            [emmy.mathbox.plot :as plot]
            [emmy.viewer :as ev]
            [emmy.viewer.physics]
            [emmy.mathbox.physics]
            [nextjournal.clerk :as clerk]))

;; ## Phase Portrait of the Pendulum

^{::clerk/visibility {:code :hide :result :hide}}
(ec/install!)

{::clerk/width :wide}

;; This example starts with an energy-based description of a pendulum, and uses
;; this to power an interactive "phase portrait".
;;
;; First, we'll write the higher-order function `T`. `T` takes the mass and
;; length of a pendulum and returns the pendulum's kinetic energy, expressed in
;; rectangular coordinates.

(defn T [m l]
  (fn [[_ _ thetadot]]
    (* 1/2 m (square (* l thetadot)))))

;; Next, we'll write down the gravitational potential energy, `V`, as a higher
;; order function. This takes gravity, mass and the pendulum's length and
;; returns a new function of `theta`, the angle of the pendulum off of center.

(defn V [g m l]
  (fn [theta]
    (* -1 m g l (cos theta))))

(defn L-pendulum [g m l]
  (- (T m l)
     (comp (V g m l) coordinate)))

;; ## Equations

;; Here's a symbolic representation of the "Lagrangian" of the system, the
;; difference between the kinetic and potential energies:

(ec/->TeX
 (simplify
  ((L-pendulum 'g 'm 'l)
   (up 't 'theta 'thetadot))))

;; And here are the equations of motion. These are equations that have to remain
;; true as the system evolves.

(ec/->TeX
 (simplify
  (((Lagrange-equations (L-pendulum 'g 'm 'l))
    (literal-function 'theta))
   't)))

{::clerk/visibility {:code :hide :result :hide}}

(defn well-axes []
  [:<>
   ['emmy.mathbox.components.plot/Grid {:axes :xy :divisions [16 20]}]
   ['emmy.mathbox.components.plot/SceneAxes
    {:x {:label false
         :end? true
         :ticks {:divisions 8
                 :width 0
                 :text-size 10
                 :label-fn '#(emmy.viewer.plot/label-pi % 2)
                 :background 0x000000}
         :color 0xffffff}
     :y {:label false :end? true
         :ticks {:divisions 4
                 :width 0
                 :offset [20 0]
                 :text-size 10
                 :background 0x000000}
         :color 0xffffff}}]])

(defn potential-line [{:keys [V atom params]}]
  (plot/of-x
   {:width 1
    :color 0x3090ff
    :y (ev/with-params {:atom atom :params params}
         V)}))

(def normalize
  '(let [f (emmy.env/principal-value Math/PI)]
     (fn [emit x y z]
       (emit (f x) y z))))

(defn well [{:keys [V !state initial-state atom params]}]
  ['mathbox.primitives/Cartesian
   {:range [[(- Math/PI) (- Math/PI 0.00001)]
            [-10 10]]
    :scale [0.48 0.25]
    :position [-0.5 -0.25]}
   (well-axes)
   (potential-line
    {:V V
     :params params
     :atom atom})
   (emmy.mathbox.physics/comet
    {:length 16
     :color 0xa0d0ff
     :size 5
     :opacity 0.99
     :post-fn normalize
     :state->xyz (ev/with-params {:atom atom :params params}
                   (fn [& params]
                     (let [V (apply V params)]
                       (fn [[_ theta]]
                         [theta (V theta) 0]))))
     :initial-state initial-state
     :atom          !state})])

(defn pendulum
  [{:keys [!state params width base-size bob-size segments]
    :or {bob-size  10
         base-size 4
         segments  1}}]
  [:<>
   ['mathbox.primitives/Array
    {:channels 2
     :items 2
     :live true
     :expr
     (list 'fn '[emit _i]
           (list 'let ['theta
                       (list 'aget (list :state
                                         (list '.-state !state))
                             1)
                       'l     (list :length (list '.-state params))]
                 '(emit 0 0)
                 '(emit (* l (Math/sin theta))
                        (* l (- (Math/cos theta))))))}]
   ['mathbox.primitives/Vector {:color 0xffffff :width width}]
   ['mathbox.primitives/Slice {:items [0 1]}]
   ['mathbox.primitives/Point {:color 0x909090 :size base-size}]
   ['mathbox.primitives/Slice {:items [1 (inc segments)]}]
   ['mathbox.primitives/Point {:color 0xffffff :size bob-size}]])

(defn pendulum-scene [{:keys [!state params]}]
  (box/cartesian
   {:range [[-1 1] [-1 1]]
    :scale [0.25 0.25]
    :position [-0.5 0.35 0]}
   (pendulum {:!state !state :params params})))

(defn phase-scene [& children]
  (apply emmy.mathbox.plot/cartesian
         {:range [[-4 4] [-8 8]]
          :scale [0.6 0.6]
          :position [0.6 0]
          :grids {:xy {:color 0x808080 :divisions 16}}
          :axes {:x {:label false
                     :color 0xffffff
                     :end? true
                     :width 2
                     :z-index 1 :z-order 5
                     :ticks {:divisions 8
                             :text-size 10
                             :offset [0 -20]
                             :background 0x000000}}
                 :y {:label false
                     :end? true
                     :width 2
                     :z-index 1
                     :z-order 5
                     :color 0xffffff
                     :ticks {:axis :y
                             :divisions 4
                             :text-size 10
                             :offset [20 0]
                             :background 0x000000}}}}
         children))

;; Given the above, we can animate a little workbench with various entries:

^{:nextjournal.clerk/width :full
  :nextjournal.clerk/visibility {:code :fold :result :show}}
(let [initial-state [0 3 0]]
  (ev/with-let [!state  {:state initial-state}
                !opts   {:length 1 :gravity 9.8 :mass 1 :simSteps 10}]
    [:<>
     (leva/controls
      {:atom !opts
       :folder {:name "Phase Portrait"}
       :schema
       {:length   {:min 0.5 :max 2 :step 0.01}
        :gravity  {:min 5 :max 15 :step 0.01}
        :mass     {:min 0.5 :max 2 :step 0.01}
        :simSteps {:min 1 :max 50 :step 1}}})

     (emmy.viewer.physics/evolve-lagrangian
      {:atom !state
       :initial-state initial-state
       :L (ev/with-params {:atom !opts :params [:gravity :mass :length]}
            L-pendulum)})

     (box/mathbox
      {:container  {:style {:height "600px" :width "100%"}}
       :threestrap {:plugins ["core" "controls" "cursor" "stats"]}
       :renderer   {:background-color 0x000000}}
      (box/layer
       (pendulum-scene {:!state !state :params !opts})

       (well
        {:V V
         :atom !opts
         :!state !state
         :initial-state initial-state
         :params [:gravity :mass :length]})

       (phase-scene
        (emmy.mathbox.physics/lagrangian-phase-vectors
         {:L (ev/with-params {:atom !opts :params [:gravity :mass :length]}
               L-pendulum)
          :initial-state initial-state
          :steps (ev/get !opts :simSteps)})

        (emmy.mathbox.physics/comet
         {:length 16
          :color 0xa0d0ff
          :size 10
          :opacity 0.99
          :state->xyz (fn [[_ theta thetadot]]
                        [theta thetadot 0])
          :post-fn       normalize
          :initial-state initial-state
          :atom          !state}))))]))
