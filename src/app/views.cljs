(ns app.views
  (:require [reagent.core :refer [atom]]
            [textures]
            [d3-selection]
            [app.state :refer [app-state]]
            [app.events :refer [increment decrement]]))

(def w (atom 10))
(defn swap-w [e] (swap! w #(.-value (.-target e))))
(def h (atom 10))
(defn swap-h [e] (swap! h #(.-value (.-target e))))
(def state (atom {}))

(defn pixel-state [x y] (get-in @state [x y]))
(defn swap-pixel [x y val] (swap! state assoc [x y] val))

(defn pixel
  [x y options]
  [:div
   {:on-click #(swap-pixel x y "blue")
    :style {:width "20px"
            :height "20px"
            :background (if-let [p (pixel-state x y)]
                          p
                          "red")
            :outline (when (:outline options) "2px solid black")}}])

(defn pixel-grid
  [w h options]
  (into [:div]
        (for [x (range w)]
          [:span {:style {:float "left"}}
           (for [y (range h)]
             ^{:key [x y]} [pixel x y options])])))

(defn width-slider
  [w]
  [:div
   "Change width"
   [:input {:type "range" :min 1 :max 100 :value w :on-change #(swap-w %)}]])

(defn height-slider
  [h]
  [:div
   "Change height"
   [:input {:type "range" :min 1 :max 100 :value h :on-change #(swap-h %)}]])

(defn app []
  [:div
   [pixel-grid @w @h {:outline true}]
   [width-slider @w]
   [height-slider @h]])



(defn simpler
  []
  )

