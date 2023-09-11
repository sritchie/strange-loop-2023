^{:nextjournal.clerk/visibility {:code :hide}}
(ns stl.viewers
  {:nextjournal.clerk/toc true}
  (:refer-clojure
   :exclude [+ - * / = zero? compare numerator denominator ref partial])
  (:require [emmy.differential]
            [mentat.clerk-utils.viewers :refer [q]]
            [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer :as-alias viewer]
            [emmy.env :as e :refer [->TeX simplify]]
            [emmy.expression :as x]
            [emmy.expression.compile :as xc]
            [emmy.value :as v]
            [reagent.core :as-alias reagent]))

(defn transform-literal [l]
  (let [simple (simplify l)]
    {:simplified_TeX (clerk/tex (->TeX simple))
     :simplified     (v/freeze simple)
     :TeX            (clerk/tex (->TeX l))
     :original       (v/freeze l)}))

(defn literal-viewer [xform]
  {:pred x/literal?
   :transform-fn (comp clerk/mark-preserve-keys
                       (clerk/update-val
                        (memoize xform)))
   :render-fn
   (q
    (fn [x]
      (viewer/html
       (reagent/with-let [!sel (reagent/atom (ffirst x))]
         [:<>
          (into
           [:div.flex.items-center.font-sans.text-xs.mb-3
            [:span.text-slate-500.mr-2 "View as:"]]
           (map (fn [[l _]]
                  [:button.px-3.py-1.font-medium.hover:bg-indigo-50.rounded-full.hover:text-indigo-600.transition
                   {:class (if (= @!sel l)
                             "bg-indigo-100 text-indigo-600"
                             "text-slate-500")
                    :on-click #(reset! !sel l)}
                   l])
                x))
          ;; I guess here the value is a data structure with its viewer info
          ;; embedded.
          [viewer/inspect-presented
           (get x @!sel)]]))))})

(def multiviewer
  (literal-viewer transform-literal))

(def diff-viewer
  {:pred emmy.differential/differential?
   :transform-fn
   (clerk/update-val
    (fn [d]
      (clerk/tex
       (e/->TeX
        (let [x  (emmy.differential/primal-part d)
              dx (emmy.differential/extract-tangent d 0)]
          (q (+ ~x (* ~dx epsilon))))))))})

(def parametric-viewer
  {:transform-fn
   (comp clerk/mark-presented
         (clerk/update-val
          (fn [m]
            (update m :f (fn [f]
                           (let [f (if (vector? f)
                                     (apply e/up f)
                                     f)]
                             (xc/compile-fn f 1 {:mode :js})))))))
   :render-fn
   '(fn [{:keys [f t]}]
      (reagent.core/with-let [f' (apply js/Function f)]
        [mafs.core/Mafs {:zoom {:min 0.1 :max 2}
                         :view-box {:x [-1 1] :y [-1 1]}}
         [mafs.coordinates/Cartesian {:subdivisions 4}]
         [mafs.plot/Parametric {:t t :xy f' :color "var(--mafs-pink)"}]]))})
