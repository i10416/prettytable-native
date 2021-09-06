import com.monovore.decline.{CommandApp, Opts}
import com.monovore.decline.Command
import commands._
import route.Route
import usecase.GenerateTableUsecase
import com.monovore.decline.{Command}
object Main {
  def main(args: Array[String]): Unit = {
    val command = Command(
      name = "pretttable-scala",
      header = "pritty print table in terminal",
      helpFlag = true
    )(Commands.appCmd)
    val usecase = new GenerateTableUsecase()
    val route = new Route(usecase)
    command.parse(args) match {
      case Right((globalFlags, commandOptions)) =>
        dispatchCmd(globalFlags, commandOptions, route)
      case Left(help) => println(help)
    }
  }

  private def dispatchCmd(
      globalFlags: GlobalFlags,
      cmd: CmdOptions,
      route: Route
  ): Unit = {
    cmd match {
      case c: ShowCmdOptions =>
        route.genTable(globalFlags.noHeaders, c) match {
          case Right(table) =>
            table.prityPrint()
          case Left(err) =>
            println(err.msg)

        }
      case c: VersionCmdOptions =>
        println("0.0.1")

    }
  }
}
