scalaVersion := "3.3.0"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true
Compile / scalaSource := baseDirectory.value / "prettytable-native" / "src" / "scala"
libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats-core" % "2.9.0",
  "com.lihaoyi" %%% "fansi" % "0.4.0",
  "com.lihaoyi" %%% "os-lib" % "0.9.1",
  "com.monovore" %%% "decline" % "2.4.1",
  "io.argonaut" %%% "argonaut" % "6.3.8",
  "org.scalatest" %% "scalatest" % "3.2.16" % Test
)

enablePlugins(ScalaNativePlugin)
