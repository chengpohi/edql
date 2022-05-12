package com.github.chengpohi.dsl.eql

import com.github.chengpohi.dsl.ElasticBase
import com.github.chengpohi.dsl.annotation.{Alias, Analyzer, CopyTo, Index}
import com.github.chengpohi.dsl.http.HttpContext
import com.github.chengpohi.parser.collection.JsonCollection
import com.github.chengpohi.parser.collection.JsonCollection.Val
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.{Request, ResponseException}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write

import java.io.Serializable
import scala.concurrent.Future
import scala.reflect.runtime.universe

/**
 * eql
 * Created by chengpohi on 6/28/16.
 */
trait EQLDefinition extends ElasticBase with EQLDsl with HttpContext {
  val ELASTIC_SHELL_INDEX_NAME: String = ".eql"
  val DEFAULT_RETRIEVE_SIZE: Int = 500
  val MAX_ALL_NUMBER: Int = 10000
  val MAX_RETRIEVE_SIZE: Int = 500

  abstract class FlagType

  case object FlagType {

    case object ALL extends FlagType

  }

  abstract class NodeType {
    def value: Array[String]
  }

  case object NodeType {

    case object ALL extends NodeType {
      override def value: Array[String] = Array()
    }

  }

  trait AttrType

  case object id extends AttrType


  case class GetActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(
        "GET",
        path);
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException => {
            EntityUtils.toString(ex.getResponse.getEntity)
          }
        }
      }
    }

    override def json: String = execute.await.json
  }

  case class HeadActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(
        "HEAD",
        path);
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException => {
            EntityUtils.toString(ex.getResponse.getEntity)
          }
        }
      }
    }

    override def json: String = execute.await.json
  }


  case class PostActionDefinition(path: String, action: Seq[JsonCollection.Val])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(
        "POST",
        path);

      val as = action.filter(_.isInstanceOf[JsonCollection.Obj]).map(_.asInstanceOf[JsonCollection.Obj].remove("plot"))
      val ps = action.filter(_.isInstanceOf[JsonCollection.Obj]).flatMap(_.asInstanceOf[JsonCollection.Obj].get("plot"))
      as match {
        case Seq() =>
        case a =>
          if (a.size > 1) {
            request.setJsonEntity(
              a.map(_.toJson)
                .mkString(System.lineSeparator()) + System.lineSeparator())
          } else if (a.size == 1) {
            request.setJsonEntity(a.head.toJson)
          }
      }
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          val entityStr = EntityUtils.toString(entity)
          if (ps.isEmpty) {
            entityStr
          } else {
            val j = parse(entityStr)
            val v = parse(ps.head.toJson)
            write(JsonAST.JObject(j.asInstanceOf[JObject].obj :+ JsonAST.JField("plot", v)))
          }
        }
        catch {
          case ex: ResponseException => {
            EntityUtils.toString(ex.getResponse.getEntity)
          }
        }
      }
    }

    override def json: String = execute.await.json
  }

  case class PutActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(
        "PUT",
        path);
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException => {
            EntityUtils.toString(ex.getResponse.getEntity)
          }
        }
      }
    }

    override def json: String = execute.await.json
  }

  case class DeleteActionDefinition(path: String, action: Option[String])
    extends Definition[String] {
    override def execute: Future[String] = {
      val request = new Request(
        "DELETE",
        path);
      request.setJsonEntity(action.orNull)
      Future {
        try {
          val entity = restClient.performRequest(request).getEntity
          EntityUtils.toString(entity)
        } catch {
          case ex: ResponseException => {
            EntityUtils.toString(ex.getResponse.getEntity)
          }
        }
      }
    }

    override def json: String = execute.await.json
  }

  trait CatDefinition extends Definition[String] {
    val path: String

    override def execute: Future[String] = {
      GET(path).execute
    }

    override def json: String = execute.await
  }

  case class CatNodesDefinition() extends CatDefinition {
    val path: String = "_cat/nodes?v"
  }

  case class CatAllocationDefinition() extends CatDefinition {
    val path: String = "_cat/allocation?v"
  }

  case class CatMasterDefinition() extends CatDefinition {
    val path: String = "_cat/master?v"
  }

  case class CatIndicesDefinition() extends CatDefinition {
    val path: String = "_cat/indices?v"
  }

  case class CatShardsDefinition() extends CatDefinition {
    val path: String = "_cat/shards?v"
  }

  case class CatCountDefinition() extends CatDefinition {
    val path: String = "_cat/count?v"
  }

  case class CatPendingTaskDefinition() extends CatDefinition {
    val path: String = "_cat/pending_tasks?v"
  }

  case class CatRecoveryDefinition() extends CatDefinition {
    val path: String = "_cat/recovery?v"
  }

  case class IndexSettingsDefinition() {
    var number_of_shards = Some(1)
    var number_of_replicas = Some(0)

    def number_of_replicas(n: Int): IndexSettingsDefinition = {
      number_of_replicas = Some(n)
      this
    }

    def number_of_shards(n: Int): IndexSettingsDefinition = {
      number_of_shards = Some(n)
      this
    }

    def toMap: Map[String, Serializable] = {
      Map("number_of_shards" -> number_of_shards.get,
        "number_of_replicas" -> number_of_replicas.get)
    }
  }

  case class NgramTokenizerDefinition(tokenizer: String) {
    var _tpe = ""
    var _tokenChars = List[String]()
    var _min_gram = 2
    var _max_gram = 2

    def tpe(tpe: String): NgramTokenizerDefinition = {
      _tpe = tpe
      this
    }

    def min_gram(min_gram: Int): NgramTokenizerDefinition = {
      _min_gram = min_gram
      this
    }

    def max_gram(max_gram: Int): NgramTokenizerDefinition = {
      _max_gram = max_gram
      this
    }

    def token_chars(token_chars: List[String]): NgramTokenizerDefinition = {
      _tokenChars = token_chars
      this
    }

    def toMap: (String, Map[String, Serializable]) = {
      tokenizer -> Map("type" -> _tpe,
        "token_chars" -> _tokenChars,
        "min_gram" -> _min_gram,
        "max_gram" -> _max_gram)
    }
  }

  case class AnalyzerDefinition(analyzer: String) {
    var _tpe = ""
    var _filter = List[String]()
    var _tokenizer = ""

    def tpe(tpe: String): AnalyzerDefinition = {
      _tpe = tpe
      this
    }

    def filter(_filters: List[String]): AnalyzerDefinition = {
      _filter = _filters
      this
    }

    def tokenizer(tokenizer: String): AnalyzerDefinition = {
      _tokenizer = tokenizer
      this
    }

    def toMap: (String, Map[String, Serializable]) = {
      analyzer -> Map("type" -> _tpe,
        "tokenizer" -> _tokenizer,
        "filter" -> _filter)
    }
  }

  case class FieldDefinition(field: String) {
    var _tpe = ""
    var _term_vector = ""
    var _store: Boolean = false
    var _analyzer: String = ""
    var _tp: String = ""

    def tpe(tpe: String): FieldDefinition = {
      _tpe = tpe
      this
    }

    def term_vector(term_vector: String): FieldDefinition = {
      _term_vector = term_vector
      this
    }

    def store(isStore: Boolean): FieldDefinition = {
      _store = isStore
      this
    }

    def analyzer(analyzer: String): FieldDefinition = {
      _analyzer = analyzer
      this
    }

    def in(tpe: String): FieldDefinition = {
      _tp = tpe
      this
    }

    def toMap: (String, Map[String, Any]) = {
      field ->
        Map("type" -> _tpe,
          "term_vector" -> _term_vector,
          "store" -> _store,
          "analyzer" -> _analyzer)
    }
  }

  case class PropertiesDefinition()

  case class MappingDefinition(analyzer: AnalyzerDefinition,
                               properties: PropertiesDefinition)

  type IndexMappings =
    (String, Map[String, (String, Map[String, Map[String, String]])])

  import scala.reflect.runtime.universe._

  case class MappingsDefinition(tpes: TypeTag[_]*) {

    def source: IndexMappings = {
      val res =
        tpes.map(
          i => {
            val indexType = getTypeName(i.tpe)

            val fields = i.tpe.members.collect {
              case m: TermSymbol if m.isVal || m.isVar =>
                val analyzer = getAnnotationByType[Analyzer]("analyzer", m)
                val copyTo = getAnnotationByType[CopyTo]("copy_to", m)
                val index = getAnnotationByType[Index]("index", m)
                val alias = getAnnotationByType[Alias]("alias", m)

                val fieldDefinition = Map("type" -> getTypeName(
                  m.typeSignature)) ++ analyzer ++ copyTo ++ index

                if (alias.isEmpty) {
                  m.name.decodedName.toString -> fieldDefinition
                } else {
                  alias("alias") -> fieldDefinition
                }
            }

            indexType -> ("properties" -> fields.toMap)
          })
      "mappings" -> res.toMap
    }

    private def getAnnotationByType[T](name: String, m: universe.TermSymbol)(
      implicit typeTag: TypeTag[T]) = {
      m.annotations
        .find(a => a.tree.tpe <:< typeTag.tpe)
        .map(a => {
          a.tree.children.tail.map {
            case Literal(Constant(c)) => c.asInstanceOf[String]
          }.head
        })
        .map(a => name -> a)
        .toMap
    }

    private def getTypeName(i: Type): String = {
      i.typeSymbol.name.decodedName.toString.toLowerCase match {
        case "string" => "text"
        case a => a
      }
    }
  }

  object Mappings {
    def apply[A](implicit typeTag: TypeTag[A]) = MappingsDefinition(typeTag)

    def apply[A, B](implicit typeTagA: TypeTag[A], typeTagB: TypeTag[B]) =
      MappingsDefinition(typeTagA, typeTagB)

    def apply[A, B, C](implicit typeTagA: TypeTag[A],
                       typeTagB: TypeTag[B],
                       typeTagC: TypeTag[C]) =
      MappingsDefinition(typeTagA, typeTagB, typeTagC)

    def apply[A, B, C, D](implicit typeTagA: TypeTag[A],
                          typeTagB: TypeTag[B],
                          typeTagC: TypeTag[C],
                          typeTagD: TypeTag[D]) =
      MappingsDefinition(typeTagA, typeTagB, typeTagC, typeTagD)

    def apply[A, B, C, D, E](implicit typeTagA: TypeTag[A],
                             typeTagB: TypeTag[B],
                             typeTagC: TypeTag[C],
                             typeTagD: TypeTag[D],
                             typeTagE: TypeTag[E]) =
      MappingsDefinition(typeTagA, typeTagB, typeTagC, typeTagD, typeTagE)

    def apply[A, B, C, D, E, F](implicit typeTagA: TypeTag[A],
                                typeTagB: TypeTag[B],
                                typeTagC: TypeTag[C],
                                typeTagD: TypeTag[D],
                                typeTagE: TypeTag[E],
                                typeTagF: TypeTag[F]) =
      MappingsDefinition(typeTagA,
        typeTagB,
        typeTagC,
        typeTagD,
        typeTagE,
        typeTagF)
  }

  trait IndexSettings {

    case class Analyzer(name: String,
                        tpe: String,
                        tokenizer: String,
                        filter: String,
                        stopwordsPath: String = "") {
      def source: (String, Map[String, AnyRef]) = {
        name -> Map("type" -> tpe,
          "tokenizer" -> tokenizer,
          "filter" -> filter.split("\\s+,\\s+"),
          "stopwords_path" -> stopwordsPath)
      }
    }

    case class Filter(name: String, tpe: String, keepwordsPath: String = "") {
      def source: (String, Map[String, String]) = {
        name -> Map("type" -> tpe, "keep_words_path" -> keepwordsPath)
      }
    }

    val analyzer: Analyzer
    val filter: Filter

    def source = {
      "settings" -> ("analysis" -> Map("analyzer" -> analyzer.source,
        "filter" -> filter.source))
    }
  }

  case class ParserErrorDefinition(parameters: Seq[Val])
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
