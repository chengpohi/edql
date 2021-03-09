package com.github.chengpohi.helper

import com.github.chengpohi.dsl.serializer.JSONOps
import org.scalatest.flatspec.AnyFlatSpec


class ResponseGeneratorTest extends AnyFlatSpec with JSONOps {
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
    val str = extractJSON(json, "_source.created_at")
    val name = extractJSON(json, "_source.name")
    assert(str === """1453476425260""")
    assert(name === """"Discuss Elastic"""")
  }
}
