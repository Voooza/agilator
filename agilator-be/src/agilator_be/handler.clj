(ns agilator-be.handler
  (:require  [immutant.web             :as web]
             [immutant.web.async       :as async]
             [immutant.web.middleware  :as web-middleware]
             [compojure.route          :as route]
             [compojure.core           :refer (ANY GET defroutes)]
             [ring.util.response       :refer (response redirect)]
             [ring.middleware.params   :refer (wrap-params)]
             [clojure.data.json        :as json]
             [clojure.string           :as s]
             [clojure.pprint           :as pp]
             [agilator-be.retro        :as retro]
             [clojure.java.io          :as io]
             [overtone.at-at           :as at])
  (:gen-class))

(defonce channels (atom {}))
(def thread-pool (at/mk-pool))

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

(defn handle-command
  [app session command]
  (println app "/" session "/" command)
  (cond
    (= app "retro") (retro/handle-command session command))
  )


(defroutes routes
  (GET "/:app/" [app] (redirect (str "/" app "/index.html")))
  (GET "/" request (redirect "/index.html"))
  (wrap-params (GET "/:app/:command{[a-z]+}" [app command] (fn [r] (handle-command app (-> r :params (get "session")) command))))
  (route/resources "/")
  
  )

(comment

  (routes {:uri "/retro/"
           :request-method :get})
  (routes {:uri "/retro/index.html"
           :request-method :get})
  (routes {:uri "/retro/export"
           :query-string "session=abcde"
           :request-method :get})
  (routes {:uri "/retro/export"
           :query-string "session=aec7c624-979c-4140-be3c-50bab739f7b3"
           :request-method :get})
  (routes {:uri "/retro/export"
           :request-method :get})
  (routes {:uri "/.git/config"
           :request-method :get})
  
  
  )

(defn string->number [str default]
  (if (s/blank? str)
    default
    (let [n (read-string str)]
      (if (number? n) n default))))

(defn -main [& args]
  
  (let [port (string->number (first args) 8080)
        stopper (at/every (* 1000 60 60) #(retro/cleanup-inactive-sessions) thread-pool)]
    (web/run
      (-> routes
          (web-middleware/wrap-session {:timeout 20})
          (web-middleware/wrap-websocket websocket-callbacks))
      {"host" "127.0.0.1", "port" port})
    (at/stop stopper)
    ))


(comment


  (-main "9090")

  (do
    (web/stop)
    (-main)
    )

  )
