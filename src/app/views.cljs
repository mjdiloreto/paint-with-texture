(ns app.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [textures]
            [d3-selection]
            [app.state :refer [app-state]]
            [app.events :refer [increment decrement]]))

(defn texture-svg
  [w h texture]
  (r/create-class {:reagent-render
                   (fn []
                     [:svg {:width w :height h :ref #(when % (.call (-> d3-selection (.select %)) texture))}
                      [:rect {:width w :height h :style {:fill (.url texture)}}]])}))



(def tex1 (.thicker (.lines textures)))
(def tex2 (.thinner (.lines textures)))

(def app-state
  (r/atom {:painting false
           :cells {2 {2 true}}
           :width 15
           :height 15}))

(defn texture-cell
  [x y]
  [:td {:on-mouse-over
        (fn [e]
          (when (:painting @app-state)
            (do
              (swap! app-state update-in [:cells x y] (constantly true)))))
        :style {:border "none" :border-collapse "collapse" :line-height 0 :cell-spacing 0 :padding 0 :margin 0}}
   [texture-svg 30 30 (if (get-in @app-state [:cells x y]) tex1 tex2)]])

(defn texture-grid
  []
  (let [no-space {:border "none" :border-collapse "collapse" :line-height 0 :cell-spacing 0 :padding 0 :margin 0}
        rows (:height @app-state)
        columns (:width @app-state)]
    [:table {:on-mouse-down #(swap! app-state update :painting not)
             :on-mouse-up #(swap! app-state update :painting not) ; probably needs to be on the highest element
             :style no-space}
     (into [:tbody]
           (for [x (range rows)]
             ^{:key x}
             (into [:tr {:style no-space}]
                   (for [y (range columns)]
                     ^{:key y} [texture-cell x y]))))]))

(defn simpler
  []
  [texture-grid  (:cells @app-state) (:painting @app-state)])

@app-state
