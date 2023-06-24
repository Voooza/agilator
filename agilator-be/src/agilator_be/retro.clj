(ns agilator-be.retro
  (:require
   [agilator-be.utils        :refer (uuid keywordize)]
   [clojure.data.json        :as json]
   [clojure.pprint           :as pp]
   [clojure.set              :as set]
   [immutant.web.async       :as async]
   [hiccup.core              :as hc]
   [clj-htmltopdf.core       :as pdf]
   [ring.util.io             :as rui]))

(defonce sessions (atom {}))
(defonce channels (atom {}))

(comment
  
  (pp/pprint @sessions)
  (pp/pprint @channels)


  
  (reset! sessions {})
  (reset! channels {})

  )

(defn send-updates
  [chan session-key]
  (async/send! chan (json/write-str {:users (vals (:users (get @sessions session-key)))
                                     :remarks (get-in @sessions [session-key :remarks])})))


(defn handle-reactions
  [msg session-key]
  (if-let [reaction (get msg "react")] 
    (swap! sessions update-in [session-key :remarks (get reaction "uuid") :reactions] assoc (get reaction "nick") (dissoc reaction "uuid"))))

(defn handle-delete
  [msg session-key]
  (if-let [uuid (get-in msg ["delete" "uuid"])]
    (swap! sessions update-in [session-key :remarks] dissoc uuid)))

(defn handle-message
  [ch msg]
  (let [nick (get msg "nick")
        session-key (get msg "session")
        session (if (get @sessions session-key)
                  (get @sessions session-key)
                  (swap! sessions assoc session-key {:users {} :remarks {}}))]
    (swap! sessions update-in [session-key :users] assoc ch nick)
    (cond (not (get @channels ch))
      (swap! channels assoc ch session-key))
    (cond (get msg "remark")
      (swap! sessions update-in [session-key :remarks] assoc (uuid) (assoc (get msg "remark") :reactions {})))
    (handle-reactions msg session-key)
    (handle-delete msg session-key)
    (doall
     (for [chan (keys (:users (get @sessions session-key)))]
       (send-updates chan session-key)))))

(defn handle-close
  [ch code reason]
  (let [session (get @channels ch)]
    (swap! channels dissoc ch)
    (swap! sessions update-in [session :users] dissoc ch)))

(defn cleanup-inactive-sessions
  []
  (println "cleaning up inactive sessions")
  (pp/pprint @sessions)
  (pp/pprint @channels)
  (let [all-session-keys (set (keys @sessions))
        active-session-keys (set (vals @channels))
        sessions-to-clean (set/difference all-session-keys active-session-keys)]
    (swap! sessions (fn [s] (apply dissoc s sessions-to-clean)))))

(comment
  
  )

(defn export-remarks
  [remarks category]
  (for [r (filter (fn [r] (= category (:cat r))) remarks)]
    (do (pp/pprint (->> r :reactions vals (map keywordize) (map :kind)))
        [:p (str (:owner r) ": " (:content r)) [:br]
         (for [[reaction count] (->> r :reactions vals (map keywordize) (map :kind) frequencies)]
           [:i  {:style "margin: 0.5em"} (str (cond (> count 1) (str count " x ")) reaction)])])))

(defn export-html
  [session-key users remarks]
  (let [kwremarks (map keywordize (vals remarks))]
    (hc/html
     [:div
      [:h1 "You had a great retrospective!"]
      [:h2 "Hope you enjoyed it as much as I did...."]
      [:p "Your sessionId: " session-key]
      [:p (str "exported on " (new java.util.Date))]
      [:h2 "Who participated?"]
      [:ul
       (for [u users]
         [:li u])]
      [:h2 {:style "color: #2ECC71"} "What went well?" ]
      (export-remarks kwremarks "www")
      [:h2 {:style "color: #922B21"} "What went badly?"]
      (export-remarks kwremarks "wth")
      [:h2 {:style "color: #1F618D"} "What shall we try to improve things?"]
      (export-remarks kwremarks "wws")
      [:h2 {:style "color: #CA6F1E"} "What confused us?"]
      (export-remarks kwremarks "wtf")
      ])))

(defn handle-command
  [session-key command]
  (if-let [session (get @sessions session-key)]
    (let [users (->> session :users vals)
          remarks (:remarks  session)]
      {:status 200
       :headers {"Content-Type" "application/pdf"}
       :body (rui/piped-input-stream
              (fn [out] (pdf/->pdf (export-html session-key users remarks) out)))})
    (str "error")
    )
  )


