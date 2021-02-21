package com.github.chengpohi.helper

import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.typesafe.config.{Config, ConfigFactory}

object EQLTestContext extends EQLConfig with EQLContext {
  override   lazy val config: Config =
    ConfigFactory.load("test_eql.conf").getConfig("eql")
}
