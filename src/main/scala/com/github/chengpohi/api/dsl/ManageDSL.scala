package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticBase
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.node.stats.{NodesStatsRequestBuilder, NodesStatsResponse}
import org.elasticsearch.action.admin.indices.stats.{IndicesStatsRequestBuilder, IndicesStatsResponse}

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
    case object ALL extends NodeType {
      override def value: Array[String] = Array()
    }
  }

  case class NodeStatsRequestDefinition(nodesStatsRequestBuilder: NodesStatsRequestBuilder) extends ActionRequest[NodesStatsResponse] {
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

  case class IndicesStatsRequestDefinition(indicesStatsRequestBuilder: IndicesStatsRequestBuilder) extends ActionRequest[IndicesStatsResponse] {
    def flag(f: FlagType) = {
      indicesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: (ActionListener[IndicesStatsResponse]) => Unit = indicesStatsRequestBuilder.execute
  }

  case object indice {
    def stats(nodeIds: List[String]) = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indices.prepareStats(nodeIds:_ *)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }
    def stats(indiceType: NodeType) = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indices.prepareStats(indiceType.value: _*)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }
  }
}
