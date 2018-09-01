package com.github.chengpohi.registry

import com.github.chengpohi.api.EQLClient
import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.parser.EQLParser
import org.elasticsearch.client.Client


trait EQLContext {
  this: EQLConfig =>
  implicit lazy val eql: EQLClient = buildClient(config)
  implicit lazy val elkParser = new EQLParser(eql)
}
