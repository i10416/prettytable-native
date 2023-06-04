package route
import usecase.GenerateTableUsecase
import commands.ShowCmdOptions
import os.Path
import model.Table
import cats.syntax.all._
import usecase.GenerateJsonTableUsecase

sealed trait ShowCSVError extends Product with Serializable {
  def msg: String
}
final case class IsNotFile(path: Path) extends ShowCSVError {
  override def msg: String =
    s"User error: $path is not a file but scalaprettyprint expects is to be a file"
}
final case class UnsupportedExt(ext: String) extends ShowCSVError {
  override def msg: String =
    s"User error: $ext is not supported. Supported Extensions: ${SupportedExts.all}"
}

final case class JsonIsNotArray() extends ShowCSVError {
  override def msg: String = "Json is not array"
}

final case class ParseError() extends ShowCSVError {
  override def msg: String = s"Parse error:  failed to parse file."
}

object SupportedExts {
  val csv = "csv"
  val json = "json"
  val all = Seq(csv, json)
}
class Route(
    generateTableUsecase: GenerateTableUsecase,
    generateJsonTableUsecase: GenerateJsonTableUsecase
) {
  def genTable(
      noHeader: Boolean,
      cmd: ShowCmdOptions
  ): Either[ShowCSVError, Table] = {
    val path = os.pwd / cmd.fileName
    if (os.isFile(path)) {
      path.ext match {
        case SupportedExts.csv =>
          generateTableUsecase.execute(path, hasHeader = !noHeader)
        case SupportedExts.json => generateJsonTableUsecase.execute(path)
        case it                 => UnsupportedExt(it).asLeft
      }
    } else {
      IsNotFile(path).asLeft
    }
  }
}
