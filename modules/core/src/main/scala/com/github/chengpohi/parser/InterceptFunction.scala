package com.github.chengpohi.parser

import com.github.chengpohi.context.EQLContext
import com.github.chengpohi.dsl.eql.{Definition, ErrorHealthRequestDefinition, PureStringDefinition}
import com.github.chengpohi.parser.collection.JsonCollection
import com.jayway.jsonpath.JsonPath
import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

trait InterceptFunction {
  val MAX_NUMBER: Int = 500

  case class CatNodesInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat nodes
    }
  }

  case class GetAllocationInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat allocation
    }
  }

  case class CatMasterInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat master
    }
  }

  case class CatIndicesInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat indices
    }
  }

  case class CatShardsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat shards
    }
  }

  case class CatCountInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat count
    }
  }

  case class CatAllocationInstruction() extends Instruction2 {
    override def name: String = "Allocation"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat allocation
    }
  }

  case class CatPendingInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat pending_tasks
    }
  }

  case class CatRecoveryInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat recovery
    }
  }

  trait Instruction2 {
    def name: String

    def execute(implicit eql: EQLContext): Definition[_]

    def vars: Seq[JsonCollection.Dynamic] = Seq()
  }

  lazy val instrumentations = ConfigFactory.load("instrumentations.json")

  case class HelpInstruction(params: Seq[String]) extends Instruction2 {
    override def name = "help"

    def execute(implicit eql: EQLContext): Definition[_] = {
      params match {
        case Seq(_) =>
          //          val example: String =
          //            instrumentations.getConfig(i).getString("example")
          //          val description: String =
          //            instrumentations.getConfig(i).getString("description")
          //          val r: Map[String, AnyRef] =
          //            Map(("example", example), ("description", description))
          PureStringDefinition("help")
        case _ =>
          PureStringDefinition("I have no idea for this.")
      }
    }
  }


  trait ScriptContextInstruction2 extends Instruction2

  case class CommentInstruction() extends ScriptContextInstruction2 {
    override def name = "comment"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition("")
    }
  }

  case class EndpointBindInstruction(endpoint: String) extends ScriptContextInstruction2 {
    override def name = "host"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$endpoint")
    }
  }

  case class TimeoutInstruction(timeout: Int) extends ScriptContextInstruction2 {
    override def name = "timeout"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"timeout $timeout")
    }
  }

  case class ImportInstruction(imp: String) extends ScriptContextInstruction2 {
    override def name = "import"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$imp")
    }
  }

  case class AuthorizationBindInstruction(auth: String) extends ScriptContextInstruction2 {
    override def name = "authorization"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$auth")
    }
  }

  case class UsernameBindInstruction(username: String) extends ScriptContextInstruction2 {
    override def name = "Username"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$username")
    }
  }

  case class PasswordBindInstruction(password: String) extends ScriptContextInstruction2 {
    override def name = "Password"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$password")
    }
  }

  case class ApiKeyIdBindInstruction(apikeyId: String) extends ScriptContextInstruction2 {
    override def name = "apikeyId"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$apikeyId")
    }
  }

  case class ApiKeySecretBindInstruction(apiSecret: String) extends ScriptContextInstruction2 {
    override def name = "secret"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$apiSecret")
    }
  }

  case class ApiSessionTokenBindInstruction(apiSessionToken: String) extends ScriptContextInstruction2 {
    override def name = "session"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$apiSessionToken")
    }
  }

  case class AWSRegionBindInstruction(awsRegion: String) extends ScriptContextInstruction2 {
    override def name = "region"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$awsRegion")
    }
  }

  case class PostActionInstruction(path: String, action: Option[Seq[JsonCollection.Val]]) extends Instruction2 {
    override def name = "post"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      val newPath = mapNewPath(eql.variables, path)

      if (newPath.startsWith("/")) {
        PostActionDefinition(newPath, action.map(_.map(_.toJson)))
      } else {
        PostActionDefinition("/" + newPath, action.map(_.map(_.toJson)))
      }
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      action.getOrElse(Seq()).flatMap(i => i.vars).filter(i => i.isInstanceOf[JsonCollection.Dynamic])
  }


  case class DeleteActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "delete"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      val newPath = mapNewPath(eql.variables, path)

      if (newPath.startsWith("/")) {
        DeleteActionDefinition(newPath, action.map(_.toJson))
      } else {
        DeleteActionDefinition("/" + newPath, action.map(_.toJson))
      }
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      action.map(i => i.vars).getOrElse(Seq()).filter(i => i.isInstanceOf[JsonCollection.Dynamic])
  }

  case class PutActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "put"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      val newPath = mapNewPath(eql.variables, path)

      if (newPath.startsWith("/")) {
        PutActionDefinition(newPath, action.map(_.toJson))
      } else {
        PutActionDefinition("/" + newPath, action.map(_.toJson))
      }
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      action.map(i => i.vars).getOrElse(Seq()).filter(i => i.isInstanceOf[JsonCollection.Dynamic])
  }

  case class GetActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "get"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      val newPath = mapNewPath(eql.variables, path)

      if (newPath.startsWith("/")) {
        GetActionDefinition(newPath, action.map(_.toJson))
      } else {
        GetActionDefinition("/" + newPath, action.map(_.toJson))
      }
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      action.map(i => i.vars).getOrElse(Seq()).filter(i => i.isInstanceOf[JsonCollection.Dynamic])
  }

  case class HeadActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "head"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      val newPath = mapNewPath(eql.variables, path)

      if (newPath.startsWith("/")) {
        HeadActionDefinition(newPath, action.map(_.toJson))
      } else {
        HeadActionDefinition("/" + newPath, action.map(_.toJson))
      }
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      action.map(i => Seq(i)).getOrElse(Seq()).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])

  }

  case class VariableInstruction(variableName: String, value: JsonCollection.Val) extends ScriptContextInstruction2 {
    override def name = "variable"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }

    override def vars: Seq[JsonCollection.Dynamic] = {
      Seq(value).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
    }
  }

  case class FunctionInstruction(funcName: String, variableNames: Seq[String], instructions: Seq[Instruction2]) extends ScriptContextInstruction2 {
    override def name = "function"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }
  }

  case class ForInstruction(tempVariable: String,
                            iterVariable: JsonCollection.Val,
                            instructions: Seq[Instruction2]) extends Instruction2 {
    override def name = "for"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      Seq(iterVariable).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(_.asInstanceOf[JsonCollection.Dynamic])
  }

  case class ReturnInstruction(value: JsonCollection.Val) extends Instruction2 {
    override def name = "return"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }

    override def vars: Seq[JsonCollection.Dynamic] = {
      Seq(value).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
    }
  }

  case class EchoInstruction(value: JsonCollection.Val) extends Instruction2 {
    override def name = "echo"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }

    override def vars: Seq[JsonCollection.Dynamic] = {
      Seq(value).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
    }
  }

  case class FunctionInvokeInstruction(funcName: String, vals: Seq[JsonCollection.Val]) extends Instruction2 {
    override def name = "functionInvoke"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }

    override def vars: Seq[JsonCollection.Dynamic] = {
      vals.filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
    }
  }

  case class ReadJSONInstruction(filePath: JsonCollection.Var) extends Instruction2 {
    override def name = "readJSONInstruction"

    def execute(implicit eql: EQLContext): Definition[_] = {
      val contextPath = eql.variables.get("CONTEXT_PATH")
      val currentDir = contextPath.map(_.asInstanceOf[JsonCollection.Str].value).map(_ + "/").getOrElse("")
      val targetPath = filePath.toJson
      val content =
        Files.readAllLines(Paths.get(currentDir + targetPath.replaceAll("^\"|\"$", "")))
          .stream()
          .collect(Collectors.joining(System.lineSeparator()))

      PureStringDefinition(content)
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      Seq(filePath).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
  }

  case class WriteJSONInstruction(filePath: JsonCollection.Val, data: JsonCollection.Val) extends Instruction2 {
    override def name = "writeJSONInstruction"

    def execute(implicit eql: EQLContext): Definition[_] = {
      val contextPath = eql.variables.get("CONTEXT_PATH")
      val currentDir = contextPath.map(_.asInstanceOf[JsonCollection.Str].value).map(_ + "/").getOrElse("")

      Files.write(
        Paths.get(currentDir + filePath.toJson.replaceAll("^\"|\"$", "")),
        data.toJson.getBytes())

      PureStringDefinition("")
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      Seq(filePath, data).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
  }

  case class JQInstruction(data: JsonCollection.Val, path: JsonCollection.Val) extends Instruction2 {
    override def name = "jqInstruction"

    def execute(implicit eql: EQLContext): Definition[_] = {
      val jsonO = data.toJson
      val jsonPath = path.toJson.replaceAll("^\"|\"$", "")
      val value = JsonPath.parse(jsonO).read(jsonPath).toString
      PureStringDefinition(value)
    }

    override def vars: Seq[JsonCollection.Dynamic] =
      Seq(path, data).filter(i => i.isInstanceOf[JsonCollection.Dynamic]).map(i => i.asInstanceOf[JsonCollection.Dynamic])
  }

  case class ErrorInstruction(error: String) extends Instruction2 {

    override def name: String = "error"

    def execute(implicit eql: EQLContext): Definition[_] = {
      ErrorHealthRequestDefinition(error)
    }
  }

  def mapRealValue(variables: scala.collection.mutable.Map[String, JsonCollection.Val],
                   v: JsonCollection.Val): Unit = {
    if (v.vars.nonEmpty) {
      v.vars.foreach(k => {
        var realValue = variables.get(k.value)
        if (realValue.isEmpty) {
          throw new RuntimeException("could not find variable: " + k.value)
        }

        if (realValue.get.isInstanceOf[JsonCollection.Fun]) {
          realValue = realValue.asInstanceOf[JsonCollection.Fun].realValue
        }

        realValue.foreach(r => {
          if (r.vars.nonEmpty) {
            r.vars.foreach(t => {
              mapRealValue(variables, t)
            })
          }
        })
        k.realValue = realValue
      })
    }
  }


  private def mapNewPath(variables: scala.collection.mutable.Map[String, JsonCollection.Val], path: String) = {
    variables.foldLeft(path)((i, o) => {
      val vName = "\\$" + o._1;
      val v = o._2 match {
        case s: JsonCollection.Str => {
          s.value
        }
        case va: JsonCollection.Var => {
          mapRealValue(variables, va)
          va.realValue match {
            case Some(fa) => fa match {
              case s: JsonCollection.Str => s.value
              case j => j.toJson
            }
            case None => va.value
          }
        }
        case s => s.toJson
      }
      i.replaceAll(vName, v)
    })
  }


  def systemFunction: Map[String, FunctionInstruction] = {
    Map(
      "jq2" -> FunctionInstruction("jq", Seq("data", "path"), Seq(JQInstruction(JsonCollection.Var("data"), JsonCollection.Var("path")))),
      "readJSON1" -> FunctionInstruction("readJSON", Seq("filePath"), Seq(ReadJSONInstruction(JsonCollection.Var("filePath")))),
      "writeJSON2" -> FunctionInstruction("writeJSON", Seq("filePath", "data"), Seq(WriteJSONInstruction(JsonCollection.Var("filePath"), JsonCollection.Var("data"))))
    )
  }
}

