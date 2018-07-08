package com.github.chengpohi.plugin.eql


import com.github.chengpohi.api.EQLClient
import com.github.chengpohi.parser.ELKParser
import com.github.chengpohi.repl.EQLInterpreter
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.support.{ActionFilters, AutoCreateIndex, HandledTransportAction}
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver
import org.elasticsearch.cluster.service.ClusterService
import org.elasticsearch.common.inject.Inject
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.script.ScriptService
import org.elasticsearch.tasks.Task
import org.elasticsearch.threadpool.ThreadPool
import org.elasticsearch.transport.TransportService

class TransportEQLAction @Inject()(
                                    settings: Settings,
                                    threadPool: ThreadPool,
                                    actionFilters: ActionFilters,
                                    indexNameExpressionResolver: IndexNameExpressionResolver,
                                    clusterService: ClusterService,
                                    scriptService: ScriptService,
                                    autoCreateIndex: AutoCreateIndex,
                                    client: Client,
                                    transportService: TransportService)
  extends HandledTransportAction[EQLRequest, EQLResponse](
    settings,
    EQLAction.NAME,
    threadPool,
    transportService,
    actionFilters,
    indexNameExpressionResolver,
    () => {
      new EQLRequest
    }) {
  implicit val eqlClient: EQLClient = new EQLClient(client)
  implicit val elkParser = new ELKParser(eqlClient)
  val interpreter = new EQLInterpreter()

  override def doExecute(task: Task,
                         eqlRequest: EQLRequest,
                         listener: ActionListener[EQLResponse]): Unit = {
    val result = interpreter.run(eqlRequest.request)
    listener.onResponse(EQLResponse(result))
  }
  override def doExecute(request: EQLRequest,
                         listener: ActionListener[EQLResponse]): Unit = {
    throw new UnsupportedOperationException("task required")
  }
}
