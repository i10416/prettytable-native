package route
import usecase.GenerateTableUsecase
import commands.ShowCmdOptions
import os.Path
import model.Table
import cats.syntax.all._

sealed trait ShowCSVError extends Product with Serializable {
  def msg: String
}
final case class IsNotFile(path: Path) extends ShowCSVError {
  override def msg: String =
    s"User error: $path is not a file but scalaprettyprint expects is to be a file"
}
final case class ParseError() extends ShowCSVError {
  override def msg: String = s"Parse error:  failed to parse file"
}
class Route(generateTableUsecase: GenerateTableUsecase) {
  def genTable(noHeader:Boolean,cmd: ShowCmdOptions): Either[ShowCSVError, Table] = {
    val path = os.pwd / cmd.fileName
    if (os.isFile(path)) {
      generateTableUsecase.execute(path,hasHeader = !noHeader)
    } else {
      IsNotFile(path).asLeft
    }
  }
}
