(ns ^{:author "Abhinav Sharma (@abhi18av)"
      :doc    "Wraps the api for https://dev.codewars.com/"}

  app.external-api.scratch

  (:require [app.secrets :as secrets]
            [app.utils :as utils :refer [namespaced-keys pull-namespaced pull-key update-if]]
            [clojure.core.async :refer [go timeout <! take!]]
            [clojure.string :as str]
            [com.wsscode.common.async-cljs :refer [go-catch <? let-chan chan? <?maybe <!maybe go-promise]]
            [com.wsscode.pathom.diplomat.http.fetch :as p.http.fetch]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.connect.graphql :as pcg]
            [com.wsscode.pathom.diplomat.http :as p.http]))



;;============= scratch =======================



(comment

  (def memory (atom {}))

  (defn api [{::keys [endpoint]}]
    (let [cors-proxy "https://cors-anywhere.herokuapp.com/"
          api-url "https://www.codewars.com/api/v1"]
      (take!
        (p.http.fetch/request-async {::p.http/url     (str cors-proxy api-url endpoint)
                                     ::p.http/headers {:Authorization secrets/token}
                                     ::p.http/as      ::p.http/json
                                     ::p.http/method  "get"})
        #(reset! memory (:com.wsscode.pathom.diplomat.http/body %)))))



  @memory

  (api {::endpoint "/users/abhi18av"})






  '())