scalaVersion := "2.13.8"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats-core" % "2.7.0",
  "com.lihaoyi" %%% "fansi" % "0.2.14",
  "com.lihaoyi" %%% "os-lib" % "0.7.8",
  "com.monovore" %%% "decline" % "2.2.0",
  "io.argonaut" %%% "argonaut" % "6.3.8",
  "org.scalatest" %% "scalatest" % "3.2.3" % Test
)

enablePlugins(ScalaNativePlugin)
