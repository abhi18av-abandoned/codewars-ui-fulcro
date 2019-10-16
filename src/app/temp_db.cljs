(ns app.temp-db)

(def user-db
  {:username            "abhi18av",
   :name                "Abhinav Sharma",
   :honor               4,
   :clan                "",
   :leaderboardPosition nil,
   :skills              ["clojure" "clojurescript" "reactjs" "fulcro" "frontend"],
   :ranks               {:overall   {:rank -8, :name "8 kyu", :color "white", :score 2},
                         :languages {:clojure {:rank -8, :name "8 kyu", :color "white", :score 2}}},
   :codeChallenges      {:totalAuthored 0, :totalCompleted 1}}
  )


(def temp-db {})