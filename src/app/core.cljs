(ns app.core
  (:require [reagent.core :as r]
            [textures]
            [d3-selection]
            [reagent.dom :as rdom]
            [app.views :as views]))

(defn ^:dev/after-load start
  []
  (rdom/render [views/simpler] (.getElementById js/document "app")))

(defn add-textured-circle-to-svg
  [svg-elt texture]
  (-> svg-elt
      (.append "circle")
      (.attr "cx" 30)
      (.attr "cy" 30)
      (.attr "r" 20)
      (.style "fill" (.url texture))))

(defn add-textured-square-to-svg
  [svg-elt texture]
  (-> svg-elt
      (.append "rect")
      (.attr "width" 30)
      (.attr "height" 30)
      (.style "fill" (.url texture))))

(defn setup-svg-grid
  [height width starting-texture]
  (doseq [h (range height)
          w (range width)]
    (let [tile (-> d3-selection (.select "#app") (.append "svg") (.attr "width" 30) (.attr "height" 30))
          _ (js/console.log tile)
          _ (.call tile starting-texture)]
      (add-textured-square-to-svg tile starting-texture))))

(defn ^:export main
  []
  #_(let
      [texture (.thicker (.lines textures))
       texture2 (.thinner (.lines textures))]
    (setup-svg-grid 5 5 texture))
  (start))

