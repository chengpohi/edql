package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticBase
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder

/**
  * elasticshell
  * Created by chengpohi on 6/26/16.
  */
trait ManageDSL extends ElasticBase {

  case class NodeStatsRequestDefine(nodesStatsRequestBuilder: NodesStatsRequestBuilder) {
    val all = nodesStatsRequestBuilder.all()
  }

  case object node {
    def stats(nodeIds: List[String]) = {
      val prepareNodesStats: NodesStatsRequestBuilder = cluster.prepareNodesStats(nodeIds: _*)
      NodeStatsRequestDefine(prepareNodesStats)
    }
  }

}
