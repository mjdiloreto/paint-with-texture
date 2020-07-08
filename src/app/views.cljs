(ns app.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [textures]
            [d3-selection]
            [app.state :refer [app-state]]
            [app.events :refer [increment decrement]]))

(def tex1 (.thicker (.lines textures)))
(def tex2 (.thinner (.lines textures)))
(def tex3 (.heavier (.circles textures)))
(def tex4 (.lighter (.circles textures)))

(def textures [tex1 tex2 tex3 tex4])

(def app-state
  (r/atom {:painting false
           :cells {2 {2 1}}
           :grid-width 15
           :grid-height 15
           :cell-width 30
           :cell-height 30
           :selected-texture 0}))

;; TODO see if moving deref outside gains performance
(defn texture-svg
  [x y]
  (r/create-class {:reagent-render
                   (fn []
                     (let [texture-for-cell (get-in @app-state [:cells x y])
                           _ (js/console.log (:selected-texture @app-state))
                           texture (if texture-for-cell
                                     (nth textures texture-for-cell)
                                     (nth textures (:selected-texture @app-state)))
                           w (:cell-width @app-state)
                           h (:cell-height @app-state)]
                       [:svg {:width w :height h :ref #(when % (.call (-> d3-selection (.select %)) texture))}
                        [:rect {:width w :height h :style {:fill (.url texture)}}]]))}))

(defn texture-cell
  [x y]
  (letfn [(paint-cell []
            (swap! app-state update-in [:cells x y] (constantly (:selected-texture @app-state))))]
    [:td {:on-mouse-over
          (fn [e] (when (:painting @app-state) (paint-cell)))
          :on-mouse-down
          (fn [e] (paint-cell))
          :style {:border "none" :border-collapse "collapse" :line-height 0 :cell-spacing 0 :padding 0 :margin 0}}
     [texture-svg x y]]))

(defn texture-grid
  []
  (let [no-space {:border "none" :border-collapse "collapse" :line-height 0 :cell-spacing 0 :padding 0 :margin 0}
        rows (:grid-height @app-state)
        columns (:grid-width @app-state)]
    [:table {:on-mouse-down #(swap! app-state update :painting not)
             :on-mouse-up #(swap! app-state update :painting not) ; probably needs to be on the highest element
             :style no-space}
     (into [:tbody]
           (for [x (range rows)]
             ^{:key x}
             (into [:tr {:style no-space}]
                   (for [y (range columns)]
                     ^{:key y} [texture-cell x y]))))]))


(defn slider
  [val text min max on-change]
  [:div
   text
   [:input {:type "range" :min min :max max :value val :on-change on-change}]])

;; TODO see if macro appropriate for callbacks
(defn grid-height-slider
  []
  (slider (:grid-height @app-state) "Change grid height" 1 25
          #(swap! app-state update :grid-height (constantly (-> % .-target .-value)))))

(defn grid-width-slider
  []
  (slider (:grid-width @app-state) "Change grid width" 1 25
          #(swap! app-state update :grid-width (constantly (-> % .-target .-value)))))

(defn cell-height-slider
  []
  (slider (:cell-height @app-state) "Change cell height" 10 50
          #(swap! app-state update :cell-height (constantly (-> % .-target .-value)))))

(defn cell-width-slider
  []
  (slider (:cell-width @app-state) "Change cell width" 10 50
          #(swap! app-state update :cell-width (constantly (-> % .-target .-value)))))

(defn selected-texture-slider
  []
  (slider (:selected-texture @app-state) "Change texture" 0 3
          #(do
             (js/console.log (-> % .-target .-value))
             (swap! app-state update :selected-texture (constantly (js/parseInt (-> % .-target .-value)))))))

(defn simpler
  []
  [:div
   [grid-width-slider]
   [grid-height-slider]
   [cell-width-slider]
   [cell-height-slider]
   [selected-texture-slider]
   [texture-grid]])

@app-state
