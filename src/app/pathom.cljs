(ns app.pathom
  (:require [app.temp-db :refer [user-db]]
            [app.utils :as utils :refer [namespaced-keys pull-namespaced pull-key update-if]]
            [clojure.core.async :refer [go timeout <! take!]]
            [clojure.string :as str]
            [com.wsscode.common.async-cljs :refer [go-catch <? let-chan chan? <?maybe <!maybe go-promise]]
            [com.wsscode.pathom.diplomat.http.fetch :as p.http.fetch]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.connect.graphql :as pcg]
            [com.wsscode.pathom.diplomat.http :as p.http]
            [app.secrets :as secrets]))


;;=============== utils ================

(declare pathom-api)

;;=============== api adapters ================

(defn adapt-ranks [a-map]
  (-> a-map
      (namespaced-keys :codewars.user)
      (pull-namespaced :codewars.user/ranks "codewars.user.ranks")))


(comment

  (-> user-db
      adapt-ranks
      #_(pull-namespaced :codewars.ranks/overall "codewars.ranks.overall")
      #_(pull-namespaced :codewars/codeChallenges "codewars.codeChallenges")
      (pc/data->shape))

  '())


;;=============== api shape ================

(def user-shape [{:codewars.user.ranks/languages [{:clojure [:color :name :rank :score]}]}
                 {:codewars.user.ranks/overall [:color :name :rank :score]}
                 :codewars.user/clan
                 {:codewars.user/codeChallenges [:totalAuthored :totalCompleted]}
                 :codewars.user/honor
                 :codewars.user/leaderboardPosition
                 :codewars.user/name
                 :codewars.user/skills
                 :codewars.user/username])


;;=============== api resolvers ================

(pc/defresolver a-user
  [env {:keys [username]}]
  {::pc/input  #{:codewars.user/username}
   ::pc/output user-shape}
  (let [cors-proxy "https://cors-anywhere.herokuapp.com/"
        api-url "https://www.codewars.com/api/v1/users/"]
    (go-catch
      (->> (p.http/request env (str cors-proxy api-url username)
                           {::p.http/accept  ::p.http/json
                            ::p.http/headers {:api-key secrets/token}}) <?maybe
           ::p.http/body
           adapt-ranks))))




;;=============== api parser ================

(def http-driver p.http.fetch/request-async)


(defonce indexes (atom {}))

(def parser
  (p/parallel-parser
    {::p/env     {::p/reader               [p/map-reader
                                            pc/parallel-reader
                                            pc/open-ident-reader
                                            p/env-placeholder-reader]
                  ::p/placeholder-prefixes #{">"}
                  ::p.http/driver          http-driver}
     ::p/plugins [(pc/connect-plugin {::pc/register [a-user]
                                      ::pc/indexes  indexes})
                  ;; plugin
                  p/error-handler-plugin
                  p/trace-plugin]}))



;;=============== api function ================

(defn pathom-api [entity query]
  (take! (parser {::p/entity (atom entity)} query) prn))

(comment

  (pathom-api {} [{[:username "abhi18av"]

                   [:codewars.user/name]}])

  '())
