(ns agilator-be.retro
  (:require  [immutant.web.async       :as async]
             [clojure.data.json        :as json]
             [clojure.string           :as s]
             [clojure.pprint           :as pp]
             [agilator-be.utils        :refer (uuid)]))

(defonce sessions (atom {}))
(defonce channels (atom {}))

(comment
 
  (pp/pprint @sessions)
  (pp/pprint @channels)
  
  (reset! sessions {})
  (reset! channels {})

  )


(defn handle-message
  [ch msg]
  (let [nick (get msg "nick")
        session-key (get msg "session")
        session (if (get @sessions session-key)
                  (get @sessions session-key)
                  (swap! sessions assoc session-key {:users {} :remarks []}))]
    (swap! sessions update-in [session-key :users] assoc ch nick)
    (if (not (get @channels ch))
      (swap! channels assoc ch session-key))
    (if (get msg "remark")
      (swap! sessions update-in [session-key :remarks] conj (assoc (get msg "remark") :uuid (uuid))))
    (doall
     (for [chan (keys (:users (get @sessions session-key)))]
       (do
         (async/send! chan (json/write-str {:users (vals (:users (get @sessions session-key)))
                                            :remarks (get-in @sessions [session-key :remarks])})))))))

(defn handle-close
  [ch code reason]
  (let [session (get @channels ch)]
    (swap! channels dissoc ch)
    (swap! sessions update-in [session :users] dissoc ch)
    (if (= 0 (count (get-in @sessions [session :users])))
      (swap! sessions dissoc session))))
