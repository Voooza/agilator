(ns agilator-be.utils)

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defn keywordize
  [mymap]
  (into {} 
        (for [[k v] mymap] 
          [(keyword k) v])))
