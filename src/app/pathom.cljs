(ns app.pathom
  (:require [app.utils :as utils :refer [namespaced-keys pull-namespaced pull-key update-if]]
            [clojure.core.async :refer [go timeout <! take!]]
            [clojure.string :as str]
            [com.wsscode.common.async-cljs :refer [go-catch <? let-chan chan? <?maybe <!maybe go-promise]]
            [com.wsscode.pathom.diplomat.http.fetch :as p.http.fetch]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.connect.graphql :as pcg]
            [com.wsscode.pathom.diplomat.http :as p.http]))


;;=============== utils ================

(declare pathom-api)

;;=============== api adapters ================


;;=============== api shape ================


;;=============== api resolvers ================


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
     ::p/plugins [(pc/connect-plugin {;;::pc/register app-registry
                                      ::pc/indexes indexes})
                  ;; plugin
                  p/error-handler-plugin
                  p/trace-plugin]}))



;;=============== api function ================

(defn pathom-api [entity query]
  (take! (parser {::p/entity (atom entity)} query) prn))


