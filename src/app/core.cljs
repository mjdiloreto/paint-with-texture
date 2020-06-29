(ns app.core
  (:require [reagent.core :as r]
            [textures]
            [d3-selection]
            [reagent.dom :as rdom]
            [app.views :as views]))

(defn ^:dev/after-load start
  []
  (rdom/render [views/simpler] (.getElementById js/document "app")))

(defn ^:export main
  []
  (let
      [texture (.thicker (.lines textures))
       example-svg-elt (-> d3-selection (.select "#app") (.append "svg"))]
    (do (.call example-svg-elt texture)
        (-> example-svg-elt
            (.append "circle")
            (.attr "cx" 30)
            (.attr "cy" 30)
            (.attr "r" 20)
            (.style "fill" (.url texture)))
        [:div "hello world"]
        #_(start))))

