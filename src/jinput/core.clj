(ns jinput.core
  (:import [net.java.games.input ControllerEnvironment Event]))

(defn controllers []
  (->> (ControllerEnvironment/getDefaultEnvironment)
       (.getControllers)
       (into [])))

(defn controller-type [controller]
  (-> controller .getType .toString))

(defn joystick? [controller]
  (= "Stick" (controller-type controller)))

(defn components [controller]
  (into {} (map #(-> [(keyword (.getName %)) %]) (.getComponents controller))))

(defn values [components]
  (reduce (fn [values [component-key component]]
            (assoc values component-key (.getPollData component)))
          {}
          components))

(let [controller (->> (controllers)
                      (filter joystick?)
                      (first))
      components (components controller)

      event (Event.)]
  
  (loop []
    (let [event-queue (.getEventQueue controller)]
      (while (.getNextEvent event-queue event)
        (let [component-name (.getName (.getComponent event))]
          (when (not (#{"x" "y" "z" "rz"} component-name))
            (println component-name  (.getValue event))))))
    
    (.poll controller)
    (println (select-keys (values components)
                          [:x :y :z :rz]))
    (Thread/sleep 1000)
    (recur)))














