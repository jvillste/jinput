(ns jinput.core
  (:import [net.java.games.input ControllerEnvironment Event]))

(defn components [controller]
  (into {} (map #(-> [(keyword (.getName %)) %]) (.getComponents controller))))

(defn controller-type [controller]
  (-> controller .getType .toString))

(defn joystick? [controller]
  (= "Stick" (controller-type controller)))

(defn controllers []
  (->> (ControllerEnvironment/getDefaultEnvironment)
       (.getControllers)
       (into [])))

(defn controller-map [controller]
  {:type (controller-type controller)
   :controller controller
   :components (components controller)})

(defn values [controller-map & keys]
  (.poll (:controller controller-map))
  (reduce (fn [values [component-key component]]
            (assoc values component-key (.getPollData component)))
          {}
          (select-keys (:components controller-map)
                       keys)))

(defn joystick-controller-map []
  (->> (controllers)
       (filter joystick?)
       (first)
       (controller-map)))

#_(let [controller (->> (controllers)
                        (filter joystick?)
                        (first)
                        (controller-map))

        event (Event.)]
    (println controller)

    (loop []
      #_(let [event-queue (.getEventQueue controller)]
          (while (.getNextEvent event-queue event)
            (let [component-name (.getName (.getComponent event))]
              (when (not (#{"x" "y" "z" "rz"} component-name))
                (println component-name  (.getValue event))))))

      #_(.poll controller)
      (println (values controller [:x :y :z :rz]))
      (Thread/sleep 1000)
      (recur)))
