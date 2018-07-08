package com.github.chengpohi.registry

import com.github.chengpohi.api.EQLClient
import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.parser.{ELKParser, InterceptFunction}

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */
trait EQLContext {
  this: EQLConfig =>
  private val client = buildClient(config)

  implicit lazy val eql: EQLClient = new EQLClient(client)
  implicit lazy val elkParser = new ELKParser(eql)
}
