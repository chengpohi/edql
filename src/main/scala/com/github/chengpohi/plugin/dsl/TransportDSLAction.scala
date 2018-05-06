package com.github.chengpohi.dsl

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.parser.ELKParser
import com.github.chengpohi.repl.ELKInterpreter
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.support.{
  ActionFilters,
  AutoCreateIndex,
  HandledTransportAction
}
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver
import org.elasticsearch.cluster.service.ClusterService
import org.elasticsearch.common.inject.Inject
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.script.ScriptService
import org.elasticsearch.tasks.Task
import org.elasticsearch.threadpool.ThreadPool
import org.elasticsearch.transport.TransportService

class TransportDSLAction @Inject()(
    settings: Settings,
    threadPool: ThreadPool,
    actionFilters: ActionFilters,
    indexNameExpressionResolver: IndexNameExpressionResolver,
    clusterService: ClusterService,
    scriptService: ScriptService,
    autoCreateIndex: AutoCreateIndex,
    client: Client,
    transportService: TransportService)
    extends HandledTransportAction[DSLRequest, DSLResponse](
      settings,
      DSLAction.NAME,
      threadPool,
      transportService,
      actionFilters,
      indexNameExpressionResolver,
      () => {
        new DSLRequest
      }) {
  implicit val dsl: ElasticDSL = new ElasticDSL(client)
  implicit val elkParser = new ELKParser(dsl)
  val interpreter = new ELKInterpreter()

  override def doExecute(task: Task,
                         dSLRequest: DSLRequest,
                         listener: ActionListener[DSLResponse]): Unit = {
    val result = interpreter.run(dSLRequest.request)
    listener.onResponse(DSLResponse(result))
  }
  override def doExecute(request: DSLRequest,
                         listener: ActionListener[DSLResponse]): Unit = {
    throw new UnsupportedOperationException("task required")
  }
}
