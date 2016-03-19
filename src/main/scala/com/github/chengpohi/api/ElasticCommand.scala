package com.github.chengpohi.api

import com.github.chengpohi.connector.ElasticClientConnector
import com.sksamuel.elastic4s.ElasticClient

/**
 * elasticshell
 * Created by chengpohi on 1/6/16.
 */
object ElasticCommand extends ElasticManagement
                      with ElasticIndexer
                      with ElasticDocUpdater
                      with ElasticDocDeleter
                      with ElasticDocQuerier
                      with ElasticAnalyzer
                      with ElasticAggs
                      with ElasticBase{
  implicit val client: ElasticClient = ElasticClientConnector.client
}
