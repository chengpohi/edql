package com.github.chengpohi.context

import com.github.chengpohi.edql.parser.json.JsonCollection
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.{Request, RequestOptions, ResponseException}

import scala.concurrent.Future

/**
 * eql
 * Created by chengpohi on 6/28/16.
 */
trait EDQLDefinition extends ElasticBase with EDQLExecutor with FutureOps {
  val KIBANA_PROXY_METHOD: String = "KIBANA_PROXY_METHOD"
  val KIBANA_PATH_PREFIX: String = "KIBANA_PATH_PREFIX"

  case class GetActionDefinition(path: String, action: Option[JsonCollection.Val])
    extends Definition[String] {
    override def execute: Future[String] = {
      val as = action.filter(_.isInstanceOf[JsonCollection.Obj]).map(_.asInstanceOf[JsonCollection.Obj])
      val request = new Request(if (kibanaProxy) "POST" else "GET", path);

      request.setOptions(RequestOptions.DEFAULT.toBuilder
        .addHeader(KIBANA_PROXY_METHOD, "GET")
        .addHeader(KIBANA_PATH_PREFIX, pathPrefix)
      );

      as match {
        case None =>
        case Some(a) =>
          request.setJsonEntity(a.toJson + System.lineSeparator())
      }
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException =>
            val responseEntityStr = EntityUtils.toString(ex.getResponse.getEntity)
            if (responseEntityStr.isBlank) {
              ex.getMessage
            } else {
              responseEntityStr
            }
        }
      }
    }

    override def json: String = execute.await.json
  }

  case class HeadActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(if (kibanaProxy) "POST" else "HEAD", path);
      request.setOptions(RequestOptions.DEFAULT.toBuilder
        .addHeader(KIBANA_PROXY_METHOD, "HEAD")
        .addHeader(KIBANA_PATH_PREFIX, pathPrefix)
      );
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException =>
            val responseEntityStr = EntityUtils.toString(ex.getResponse.getEntity)
            if (responseEntityStr.isBlank) {
              ex.getMessage
            } else {
              responseEntityStr
            }
        }
      }
    }

    override def json: String = execute.await.json
  }


  case class PostActionDefinition(path: String, action: Seq[JsonCollection.Val])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request("POST", path);

      request.setOptions(RequestOptions.DEFAULT.toBuilder
        .addHeader(KIBANA_PROXY_METHOD, "POST")
        .addHeader(KIBANA_PATH_PREFIX, pathPrefix)
      );

      val as = action.filter(_.isInstanceOf[JsonCollection.Obj]).map(_.asInstanceOf[JsonCollection.Obj])
      as match {
        case Seq() =>
        case a =>
          if (a.size > 1) {
            request.setJsonEntity(
              a.map(i => {
                trimQueryClause(i).toJson
              }).mkString(System.lineSeparator()) + System.lineSeparator())
          } else if (a.size == 1) {
            val h = trimQueryClause(a.head)
            request.setJsonEntity(h.toJson + System.lineSeparator())
          }
      }
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        }
        catch {
          case ex: ResponseException =>
            val responseEntityStr = EntityUtils.toString(ex.getResponse.getEntity)
            if (responseEntityStr.isBlank) {
              ex.getMessage
            } else {
              responseEntityStr
            }
        }
      }
    }

    private def trimQueryClause(i: JsonCollection.Obj): JsonCollection.Obj = {
      val emptyQueryClause = i.get("query").flatMap(_ match {
        case value: JsonCollection.Var =>
          value.realValue
        case a => Some(a)
      }).filter(i => i.isInstanceOf[JsonCollection.Obj]).exists(i => {
        i.asInstanceOf[JsonCollection.Obj].value.isEmpty
      })

      emptyQueryClause match {
        case true => i.remove("query")
        case false => i
      }
    }

    override def json: String = execute.await.json
  }

  case class PutActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(if (kibanaProxy) "POST" else "PUT", path);
      request.setOptions(RequestOptions.DEFAULT.toBuilder
        .addHeader(KIBANA_PROXY_METHOD, "PUT")
        .addHeader(KIBANA_PATH_PREFIX, pathPrefix)
      );
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException =>
            val responseEntityStr = EntityUtils.toString(ex.getResponse.getEntity)
            if (responseEntityStr.isBlank) {
              ex.getMessage
            } else {
              responseEntityStr
            }
        }
      }
    }

    override def json: String = execute.await.json
  }

  case class DeleteActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(if (kibanaProxy) "POST" else "DELETE", path);
      request.setOptions(RequestOptions.DEFAULT.toBuilder
        .addHeader(KIBANA_PROXY_METHOD, "DELETE")
        .addHeader(KIBANA_PATH_PREFIX, pathPrefix)
      );
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException =>
            val responseEntityStr = EntityUtils.toString(ex.getResponse.getEntity)
            if (responseEntityStr.isBlank) {
              ex.getMessage
            } else {
              responseEntityStr
            }
        }
      }
    }

    override def json: String = execute.await.json
  }


  case class ParserErrorDefinition(parameters: Seq[JsonCollection.Val])
    extends Definition[Map[String, AnyRef]] {

    override def execute: Future[Map[String, AnyRef]] = {
      val res = List("error_msg", "caused_by")
        .zip(parameters.take(2).map(_.extract[String]).toList)
        .toMap
      Future.successful(res)
    }

    override def json: String = execute.await.json
  }

}
