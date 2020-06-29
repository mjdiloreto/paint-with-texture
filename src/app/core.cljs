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
       example-svg-elt (.select d3-selection "#example")]
    (do (.call example-svg-elt texture)
        (.style (.append example-svg-elt "circle")
                "fill"
                (.url texture))
        [:div "hello world"]
        #_(start))))

