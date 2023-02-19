(ns agilator-be.utils)

(defn uuid [] (.toString (java.util.UUID/randomUUID)))
