(ns zero-one.geni.core.window
  (:require
   [zero-one.geni.core.column :refer [->col-array]]
   [zero-one.geni.docs :as docs]
   [zero-one.geni.utils :refer [ensure-coll]])
  (:import
   (org.apache.spark.sql.expressions Window)))

(defn- new-window []
  (Window/partitionBy (->col-array [])))

(defn- set-partition-by [window-spec exprs]
  (.partitionBy window-spec (->col-array exprs)))

(defn- set-order-by [window-spec exprs]
  (.orderBy window-spec (->col-array exprs)))

(defn- set-range-between [window-spec range-map]
  (.rangeBetween window-spec (:start range-map) (:end range-map)))

(defn- set-rows-between [window-spec range-map]
  (.rowsBetween window-spec (:start range-map) (:end range-map)))

(defn window [{:keys [partition-by order-by range-between rows-between]}]
  (-> (new-window)
      (cond-> partition-by (set-partition-by (ensure-coll partition-by)))
      (cond-> order-by (set-order-by (ensure-coll order-by)))
      (cond-> range-between (set-range-between range-between))
      (cond-> rows-between (set-rows-between rows-between))))

(defn over [column window-spec] (.over column window-spec))

(def unbounded-following (Window/unboundedFollowing))

(def unbounded-preceding (Window/unboundedPreceding))

(def current-row (Window/currentRow))

(defn windowed
  "Shortcut to create WindowSpec that takes a map as the argument.

  Expected keys:  [:partition-by :order-by :range-between :rows-between]"
  [options]
  (over (:window-col options)
        (window (select-keys options [:partition-by
                                      :order-by
                                      :range-between
                                      :rows-between]))))

;; Docs
(docs/alter-docs-in-ns!
 'zero-one.geni.core.window
 [(-> docs/spark-docs :methods :core :window)
  (-> docs/spark-docs :classes :core :window)])

(docs/add-doc!
 (var over)
 (-> docs/spark-docs :methods :core :column :over))
