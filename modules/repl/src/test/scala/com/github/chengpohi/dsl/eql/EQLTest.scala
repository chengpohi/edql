package com.github.chengpohi.dsl.eql

import com.github.chengpohi.helper.EQLTestTrait

/**
  * eql
  * Created by chengpohi on 9/22/16.
  */
case class TestMap(id: Int,
                   hello: String,
                   foo: String,
                   name: String,
                   pp: Option[String],
                   tt: Option[Int],
                   list: List[String])

case class TestMapScore(id: String,
                        hello: String,
                        foo: String,
                        name: String,
                        pp: Option[String],
                        tt: Option[Int],
                        list: List[String],
                        score: Long)

case class User(name: String,
                email: Option[String] = None,
                password: Option[String] = None,
                token: Option[String] = None)

class EQLTest extends EQLTestTrait {
}
