package com.github.chengpohi.dsl.eql

import com.github.chengpohi.parser.collection.JsonCollection.Val

private[dsl] trait EQLDsl extends EQLExecutor with FutureOps {

  implicit class IndexNameAndIndexType(indexName: String) {
    def /(indexType: String): IndexPath = {
      IndexPath(indexName, indexType)
    }

    def /(indexType: Option[String]): IndexPath = {
      IndexPath(indexName, indexType.getOrElse("*"))
    }
  }

  implicit class IndexNameAndIndexTypeVal(indexName: Val) {
    def /(indexType: Val): IndexPath = {
      IndexPath(indexName.extract[String], indexType.extract[String])
    }

    def /(indexType: String): IndexPath = {
      IndexPath(indexName.extract[String], indexType)
    }
  }

  case class IndexPath(indexName: String, indexType: String)

  trait OperationStatus

  case object exist extends OperationStatus

}
