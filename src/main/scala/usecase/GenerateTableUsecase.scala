package usecase

import model.Table
import route.{ShowCSVError, ParseError, IsNotFile}
import cats.syntax.all._

import os.Path
import model._
import scala.collection.mutable.{ArrayBuffer => MArrayBuffer}
import scala.util.{Success, Failure}
class GenerateTableUsecase {
  def execute(filePath: Path,hasHeader:Boolean): Either[ShowCSVError, Table] = {
    var columnWidths = MArrayBuffer[Int]()
    var rows = MArrayBuffer[Row]()
    os.read.lines.stream(filePath).foreach { line =>
      val fields = line.stripLineEnd.split(",")
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
      rows += Row(cells.toArray)
    }
    val table = Table(columnWidths.toSeq, hasHeader, rows.toSeq)
    Right(table)
  }

}
