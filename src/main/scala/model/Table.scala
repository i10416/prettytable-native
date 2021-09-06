package model

import scala.util.Try
import scala.collection.mutable.{ArrayBuffer => MArrayBuffer}
import os.Path
import fansi._

case class Table(
    columnWidths: Seq[Int],
    hasHeader: Boolean = true,
    rows: Seq[Row],
    rowSeparator: String = "-",
    lineEnd: String = "\n"
) {
  def prityPrint(): Unit = {
    val header = rows.headOption
      .flatMap { row => if (hasHeader) Some(row) else None }
      .fold(new StringBuffer("")) { row =>
        val sb = new StringBuffer()
        sb.append(Color.LightBlue(row.prettyFormat(columnWidths)))
        sb.append(
          columnWidths
            .map(width => fansi.Color.LightBlue("=").toString * width)
            .mkString(fansi.Color.LightBlue("+").toString())
        ).append(lineEnd)
        sb
      }
    val styled = rows
      .drop(if (hasHeader) 1 else 0)
      .take(if (hasHeader) rows.length - 2 else rows.length - 1)
      .foldLeft(new StringBuffer("")) { (acc, row) =>
        acc.append(row.prettyFormat(columnWidths))
        val rowSep =
          columnWidths.map(width => rowSeparator * width).mkString("+")
        acc.append(rowSep).append(lineEnd)
        acc
      }
      .append(rows.last.prettyFormat(columnWidths))
      .append(lineEnd)
    print(header.append(styled))
  }
}

object Table {
  type Cell = String

  object Cell {
    def apply(s: String): Table.Cell = s
  }

}

case class Header(cells: Seq[Table.Cell])
