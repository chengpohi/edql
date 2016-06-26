package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticBase
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.node.stats.{NodesStatsRequestBuilder, NodesStatsResponse}

/**
  * elasticshell
  * Created by chengpohi on 6/26/16.
  */
trait ManageDSL extends ElasticBase {
  abstract class FlagType
  case object FlagType {
    case object ALL extends FlagType
  }

  abstract class NodeType {
    def value: Array[String]
  }
  case object NodeType {
    case object ALL extends NodeType{
      override def value: Array[String] = Array()
    }
  }

  case class NodeStatsRequestDefinition(nodesStatsRequestBuilder: NodesStatsRequestBuilder) extends ActionRequest[NodesStatsResponse]{
    def flag(f: FlagType) = {
      nodesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: (ActionListener[NodesStatsResponse]) => Unit = nodesStatsRequestBuilder.execute
  }

  case object node {
    def stats(nodeIds: List[String]) = {
      val prepareNodesStats: NodesStatsRequestBuilder = cluster.prepareNodesStats(nodeIds: _*)
      NodeStatsRequestDefinition(prepareNodesStats)
    }
    def stats(nodeIds: NodeType) = {
      val prepareNodesStats: NodesStatsRequestBuilder = cluster.prepareNodesStats(nodeIds.value: _*)
      NodeStatsRequestDefinition(prepareNodesStats)
    }
  }
}
