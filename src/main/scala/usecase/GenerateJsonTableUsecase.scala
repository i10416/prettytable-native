package usecase

import model.Table
import route.{ShowCSVError, ParseError, IsNotFile}
import cats.syntax.all._
import os.Path
import argonaut._
import model._
import scala.collection.mutable.{ArrayBuffer => MArrayBuffer}
import scala.util.{Success, Failure}
import argonaut.JNull
import argonaut.JBool
import argonaut.JNumber
import argonaut.JString
import argonaut.JArray
import argonaut.JObject
import route.JsonIsNotArray
class GenerateJsonTableUsecase {
  def execute(filePath: Path): Either[ShowCSVError, Table] = {
    var columnWidths = MArrayBuffer[Int]()
    var rowBuffer = MArrayBuffer[Row]()

    // TODO[improve](110416): json parse using stream
    val rawValue = os.read(filePath)
    val json = Parse.parse(rawValue)
    json match {
      case Left(_) => Left(ParseError())
      case Right(j) if j.isArray => {
        val Some(jsonArray) = j.array
        // collect json keys.
        val keys = jsonArray
          .foldLeft(Set.empty[String]) { case (acc, j) =>
            j.obj match {
              case Some(jobj) => {
                acc.union(jobj.fieldSet)
              }
              case _ => acc
            }
          }
          .toList

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

        table.foreach { fields =>
          if (columnWidths.isEmpty) {
            columnWidths = MArrayBuffer.fill[Int](fields.length)(0)
          }
          var cells = MArrayBuffer[Table.Cell]()
          fields.zip(columnWidths).zipWithIndex.foreach {
            case ((field, columnWidth), idx) =>
              if (field.length > columnWidth) {
                columnWidths.update(idx, field.length)
              }
              cells += Table.Cell(field)
          }
          rowBuffer += Row(cells.toArray)
        }
        val result = Table(columnWidths.toSeq, true, rowBuffer.toSeq)
        Right(result)

      }
      case _ => Left(JsonIsNotArray())
    }
  }
}
