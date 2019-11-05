package com.github.chengpohi.dsl.serializer

import org.elasticsearch.search.SearchHit
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JObject
import org.json4s.jackson.JsonMethods._

class SearchHitSerializer
    extends CustomSerializer[SearchHit](format =>
      ({
        case JObject(x) => null
      }, {
        case x: SearchHit => {
          parse(x.getSourceAsString)
        }
      }))
