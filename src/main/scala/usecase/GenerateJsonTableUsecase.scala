package usecase

import model.Table
import route.{ShowCSVError, ParseError, IsNotFile}
import cats.syntax.all._
import os.Path
import argonaut._
import model._
import scala.collection.mutable.{ArrayBuffer => MArrayBuffer}
import scala.util.{Success, Failure}
import route.JsonIsNotArray
class GenerateJsonTableUsecase {
  def execute(filePath: Path): Either[ShowCSVError, Table] = {

    // TODO[improve](110416): json parse using stream
    val rawValue = os.read(filePath)
    val json = Parse.parse(rawValue)
    json match {
      case Left(_) => Left(ParseError())
      case Right(j) if j.isArray => {
        val Some(jsonArray) = j.array: @unchecked
        // collect json keys.
        val keys = (for {
          json <- jsonArray
          obj <- json.obj
        } yield obj.fieldSet).combineAll.toList

        def formatJson(key: Json.JsonField, json: Json): Option[String] = {
          for {
            jObj <- json.obj
            value <- jObj.apply(key)
            str =
              if (value.isArray || value.isObject) "..."
              else /*bool,number, and string*/ value.toString()
          } yield str
        }

        val rows: List[Map[String, Option[String]]] = jsonArray
          .foldLeft(List.empty[Map[String, Option[String]]]) {
            case (list, item) =>
              keys.foldLeft(Map.empty[String, Option[String]]) {
                case (_row, key) =>
                  _row.updated(
                    key,
                    formatJson(key, item)
                  )
              } :: list
          }
          .reverse

        val table = keys :: rows.map { row =>
          keys.map { key => row(key).getOrElse(" - ") }
        }

        val result = Table.build(table)
        Right(result)

      }
      case _ => Left(JsonIsNotArray())
    }
  }
}
