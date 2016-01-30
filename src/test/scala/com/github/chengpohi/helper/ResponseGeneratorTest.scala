package com.github.chengpohi.helper

import org.scalatest.FlatSpec

/**
 * elasticshell
 * Created by chengpohi on 1/28/16.
 */
class ResponseGeneratorTest extends FlatSpec {
  val responseGenerator = new ResponseGenerator

  import responseGenerator._

  "extractJSON" should "extract json by paths" in {
    val json =
      """{"_index":"chengpohi",
        |"_type":"bookmark",
        |"_id":"AVJp8i4tbH3rPTRupF0V",
        |"_version":1,
        |"found":true,
        |"_source":{
        |"name":"Discuss Elastic",
        |"url":"https://discuss.elastic.co/",
        |"created_at":1453476425260,
        |"_tab_id":"AU5RuSdyyYFDwCsP6Ad5"
        |}
        |}""".stripMargin
    val str = extractJSON(json, "created_at")
    assert(str == """[{"created_at":1453476425260}]""")
  }
}
