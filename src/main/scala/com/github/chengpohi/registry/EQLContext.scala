package com.github.chengpohi.registry

import com.github.chengpohi.api.EQLClient
import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.parser.ELKParser
import org.elasticsearch.client.Client


trait EQLContext {
  this: EQLConfig =>
  val client: Client = buildClient(config)

  implicit lazy val eql: EQLClient = new EQLClient(client)
  implicit lazy val elkParser = new ELKParser(eql)
}
