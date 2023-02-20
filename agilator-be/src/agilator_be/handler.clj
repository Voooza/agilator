(ns agilator-be.handler
  (:require  [immutant.web             :as web]
             [immutant.web.async       :as async]
             [immutant.web.middleware  :as web-middleware]
             [compojure.route          :as route]
             [compojure.core           :refer (ANY GET defroutes)]
             [ring.util.response       :refer (response redirect)]
             [clojure.data.json        :as json]
             [clojure.string           :as s]
             [clojure.pprint           :as pp]
             [agilator-be.retro        :as retro]
             [clojure.java.io :as io])
  (:gen-class))

(defonce channels (atom {}))

(comment

  (pp/pprint @channels)

  (reset! channels {})
  )

(defn handle-message
  [ch m]
  (let [msg (try (json/read-str m) (catch Exception e ""))
        app (get msg "app")]
    (cond
      (= app "retro") (do
                        (if (not (get @channels ch))
                          (swap! channels assoc ch retro/handle-close))
                        (retro/handle-message ch msg))
      :else (async/send! ch (json/write-str {:infra true :message "unmapped app!"})))))

(defn handle-close
  [ch {:keys [code reason]}]
  (let [close-handler (get @channels ch)]
    (swap! channels dissoc ch)
    (if close-handler
      (close-handler ch code reason))))

(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open   (fn [channel]
                (async/send! channel (json/write-str {:infra true :message "Ready!"})))
   :on-close   handle-close 
   :on-message handle-message})

(defn handle-resource
  [r filext]
  (let [app (->> r :params :app)
        file (-> r :params :* (str "." filext))
        mime (cond
               (= "js" filext)   "application/javascript"
               (= "css" filext)  "text/css"
               (= "html" filext) "text/html"
               :else             (str "image/" filext))]
    {:status 200
     :headers {"Content-Type" mime}
     :body (slurp (io/resource (str "public/" app "/" file)))})
  )

(defn handle-html
  [r]
  (let [app (->> r :params :app)
        mime "text/html"]
    {:status 200
     :headers {"Content-Type" mime}
     :body (slurp (io/resource (str "public/" app "/index.html")))}))

(defroutes routes
  
  ;; (GET "/" request (root request))
  ;; (GET "/retro" request (retro request))
  ;;  (GET "/retro/*" request (retro request))
  
  (GET "/:app/" request (fn [r] (handle-html r)))
  (GET "/:app/*.js" request (fn [r] (handle-resource r "js")))
  (GET "/:app/*.css" request (fn [r] (handle-resource r "css")))
  (GET "/:app/*" request (fn [r] (handle-html r)))
  
  (route/resources "/")
  
  )

(defn string->number [str default]
  (if (s/blank? str)
    default
    (let [n (read-string str)]
      (if (number? n) n default))))

(defn -main [& args]
  (let [port (string->number (first args) 8080)]
    (web/run
      (-> routes
          (web-middleware/wrap-session {:timeout 20})
          ;; wrap the handler with websocket support
          ;; websocket requests will go to the callbacks, ring requests to the handler
          (web-middleware/wrap-websocket websocket-callbacks))
      {"host" "0.0.0.0", "port" port})))


(comment


  (-main "9090")


  
  (do
    (web/stop)
    (-main)
    )

  )
