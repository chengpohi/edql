package com.github.chengpohi.plugin

import java.util
import java.util.function.Supplier

import com.github.chengpohi.plugin.eql.{EQLAction, RestEQLAction, TransportEQLAction}
import org.elasticsearch.action.{ActionRequest, ActionResponse}
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver
import org.elasticsearch.cluster.node.DiscoveryNodes
import org.elasticsearch.common.settings._
import org.elasticsearch.plugins.ActionPlugin.ActionHandler
import org.elasticsearch.plugins.{ActionPlugin, Plugin}
import org.elasticsearch.rest.{RestController, RestHandler}

class EQLPlugin extends Plugin with ActionPlugin {
  val NAME = "dsl"

  override def getActions
    : util.List[ActionHandler[_ <: ActionRequest, _ <: ActionResponse]] = {
    util.Arrays.asList(
      new ActionHandler(EQLAction.INSTANCE, classOf[TransportEQLAction])
    )
  }

  //  override def getNamedWriteables: util.List[NamedWriteableRegistry.Entry] = {
  //    singletonList(
  //      new NamedWriteableRegistry.Entry(
  //        classOf[Task.Status],
  //        BulkByScrollTask.Status.NAME,
  //        new Reader[BulkByScrollTask.Status] {
  //          @throws[IOException]
  //          def read(in: StreamInput): BulkByScrollTask.Status = {
  //            new BulkByScrollTask.Status(in)
  //          }
  //        }))
  //  }

  override def getRestHandlers(
      settings: Settings,
      restController: RestController,
      clusterSettings: ClusterSettings,
      indexScopedSettings: IndexScopedSettings,
      settingsFilter: SettingsFilter,
      indexNameExpressionResolver: IndexNameExpressionResolver,
      nodesInCluster: Supplier[DiscoveryNodes]): util.List[RestHandler] = {
    util.Arrays.asList(
      RestEQLAction(settings, restController)
    )
  }
  //
  //  override def getSettings: util.List[Setting[_]] = {
  //    singletonList(TransportReindexAction.REMOTE_CLUSTER_WHITELIST)
  //  }
}
