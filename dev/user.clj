(ns user
  (:require [emmy.clerk :as ec]
            [emmy.expression.render :as xr]
            [nextjournal.clerk.config :as cc]))

(alter-var-root
 #'xr/*TeX-vertical-down-tuples*
 (constantly true))

(alter-var-root
 #'cc/*bounded-count-limit*
 (constantly 2))

(def serve-defaults
  {:port 7777
   :watch-paths ["notebooks"]
   :browse? true})

(def static-defaults
  {:browse? false
   :paths ["notebooks/**.clj"]
   :git/url "https://github.com/sritchie/strange-loop-2023"})

(defn serve!
  "Alias of [[emmy.clerk/serve!]] with [[defaults]] supplied as default arguments.

  Any supplied `opts` overrides the defaults."
  ([] (serve! {}))
  ([opts]
   (ec/serve!
    (merge serve-defaults opts))))

(def ^{:doc "Alias for [[emmy.clerk/halt!]]."}
  halt!
  ec/halt!)

(defn build!
  "Alias of [[emmy.clerk/build!]] with [[static-defaults]] supplied as default
  arguments.

  Any supplied `opts` overrides the defaults."
  [opts]
  (ec/build!
   (merge static-defaults opts)))
