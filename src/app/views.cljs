(ns app.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [textures]
            [d3-selection]
            [app.state :refer [app-state]]
            [app.events :refer [increment decrement]]))

(def w (r/atom 10))
(defn swap-w [e] (swap! w #(.-value (.-target e))))
(def h (r/atom 10))
(defn swap-h [e] (swap! h #(.-value (.-target e))))
(def state (r/atom {}))

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


(defn simple-circle
  []
  [:circle {:cx 30 :cy 30 :r 30 :fill "red"}])

(defn textured-circle-svg
  []
  (r/with-let [texture (.thicker (.lines textures))
               this-node (r/current-component) ;(rdom/dom-node)
               _ (.call this-node texture)]
    [:svg
     [:circle {:cx 30 :cy 30 :r 30 :fill "blue" #_(.url texture)}]]))

(defn wrap-circle-with-texture
  [svg-elt circle-elt]
  (let [texture (.thicker (.lines textures))
        selected-circle (-> d3-selection (.select circle-elt))
        selected-svg (-> d3-selection (.select svg-elt))]
    (.call selected-svg texture)
    (.style selected-circle "fill" (.url texture))
    (.append selected-svg selected-circle)
    selected-svg))

(comment
  (wrap-circle-with-texture [:svg] (simple-circle)))

;; https://stackoverflow.com/questions/39831137/force-reagent-component-to-update-on-window-resize
(defn get-client-rect [node]
  (let [r (.getBoundingClientRect node)]
    {:left (.-left r), :top (.-top r) :right (.-right r) :bottom (.-bottom r) :width (.-width r) :height (.-height r)}))

(defn size-comp []
  (r/with-let [size (r/atom nil)
               this (r/current-component)
               handler #(reset! size (get-client-rect (rdom/dom-node this)))
               _ (.addEventListener js/window "resize" handler)]
    [:div "new size " @size]
    (finally (.removeEventListener js/window "resize" handler))))

(defn i-show-my-size []
  (let [size (r/atom nil)]
    (r/create-class {:component-will-mount
                     (fn [this]
                       (set! (.-onresize js/window)
                             (r/force-update this)))
                     :reagent-render
                     (fn []
                       [:div {:ref #(when % (reset! size (let [bb (.getBoundingClientRect %)]
                                                           [(.-width bb) (.-height bb)])))}
                        (prn-str @size)])})))

(defn texture-svg
  [w h texture]
  (r/create-class {:reagent-render
                   (fn []
                     [:svg {:width w :height h :ref #(when % (.call (-> d3-selection (.select %)) texture))}
                      [:rect {:width w :height h :style {:fill (.url texture)}}]])}))

(defn texture-grid
  [rows columns texture]
  (let [no-space {:border "none" :border-collapse "collapse" :cell-spacing 0 :padding 0 :margin 0}]
    [:table {:style no-space}
     [:tbody
      (for [x (range rows)]
        ^{:key x} [:tr {:style no-space}
         (for [y (range columns)]
           ^{:key y} [:td {:style no-space} [texture-svg 30 30 texture]])])]]))

(defn simpler
  []
  (let [t (.thicker (.lines textures))]
    [texture-grid 5 5 t])
  #_[:div
   [simple-svg 30 30]
   [i-show-my-size]])

(comment
  (let
      [texture (.thicker (.lines textures))
       example-svg-elt (-> d3-selection (.select "#app") (.append "svg"))]
    (.call example-svg-elt texture)
    (-> [:svg {:id "thisexample"}]
        (.append "circle")
        (.attr "cx" 30)
        (.attr "cy" 30)
        (.attr "r" 20)
        (.style "fill" (.url texture)))))
