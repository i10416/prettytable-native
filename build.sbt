scalaVersion := "2.13.6"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats-core" % "2.6.1",
  "com.lihaoyi" %%% "fansi" % "0.2.10",
  "com.lihaoyi" %%% "os-lib" % "0.7.2",
  "com.monovore" %%% "decline" % "2.1.0",
  "io.argonaut" %%% "argonaut" % "6.3.6",
  "org.scalatest" %% "scalatest" % "3.2.11" % Test
)

enablePlugins(ScalaNativePlugin)
