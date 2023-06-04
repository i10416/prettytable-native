
# Write a Simple CLI Application with Scala Native
This repository shows a simple example of writing a standalone CLI application with scala-native.

Compared to using GraalVM Native Image, there is few gotcha in writing and building scala-native application! 
## Goal
Our goal is to write a toy application which pretty-prints a csv or json file in your terminal.

Input: test.csv
```
id,name,icon,comment,favorite
1,tama,😼,meow,matatabi
2,pochi,🐶,bowwow,🦴
```
Output
```
prettytable test.csv
id|name |icon|comment|favorite
==+=====+====+=======+========
1 |tama |😼  |meow   |matatabi
--+-----+----+-------+--------
2 |pochi|🐶  |bowwow |🦴
```

Output: test.json
```
prettytable test.json
website        |name                      |email                      |username          |company|id|address|phone
===============+==========================+===========================+==================+=======+==+=======+=======================
"hildegard.org"|"Leanne Graham"           |"Sincere@april.biz"        |"Bret"            |...    |1 |...    |"1-770-736-8031 x56442"
---------------+--------------------------+---------------------------+------------------+-------+--+-------+-----------------------
"anastasia.net"|"Ervin Howell"            |"Shanna@melissa.tv"        |"Antonette"       |...    |2 |...    |"010-692-6593 x09125"
---------------+--------------------------+---------------------------+------------------+-------+--+-------+-----------------------
"ramiro.info"  |"Clementine Bauch"        |"Nathan@yesenia.net"       |"Samantha"        |...    |3 |...    |"1-463-123-4447"
---------------+--------------------------+---------------------------+------------------+-------+--+-------+----------------------


```
## Getting Started
To write a scala-native application, set up your dev environment according to following steps. 
### Install Scala

```sh
curl -fLo cs https://git.io/coursier-cli-"$(uname | tr LD ld)"
chmod +x cs
./cs install
rm ./cs
cs setup
```

### Install llvm and c++

Ubuntu

```sh
sudo bash -c "$(wget -O - https://apt.llvm.org/llvm.sh)"
```
or
```sh
sudo apt install clang
sudo apt install libgc-dev # optional
```

MacOS

```sh
brew install llvm
brew install bdw-gc # optional
```

Then, export CLANG_PATH and CLANGPP_PATH
~/.bashrc
```
export CLANG_PATH=/path/to/clang-<version>
export CLANGPP_PATH=/path/to/clang++-<version>

```

### create project

```sh
sbt new scala-native/scala-native.g8
```

Before writing your application, run the default application to test your environment. If it successfully compiles, it will show a greeting message.
```sh
cd <project name>
sbt run
```

## Write Application

We use following dependencies to build the application. I recommend you have a quick look at their documentations before start.

- [cats](https://typelevel.org/cats/): It offers functional data structures and syntax.
- [decline](https://ben.kirw.in/decline/): It enables us to handle command-line arguments in a functional way.
- [oslib](https://github.com/com-lihaoyi/os-lib): simple and unopinionated i/o utilities.
- [fansi](https://github.com/com-lihaoyi/fansi): decorate terminal output.
- [argonaut](http://argonaut.io/): Purely Functional JSON in Scala

### build declinenative locally

Decline supports scala-native, but it has yet to offer artifacts for scala-native 0.4 with scala 2.13.x. Thus, we manually build it and use the local artifact.

```sh
git clone git@github.com:monovore/decline
cd decline

sbt
> +declinenative/compile
> +declinenative/publishLocal
```

Then, add libraryDependencies to your `build.sbt`.

```scala
scalaVersion := "3.3.0"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats-core" % "2.9.0",
  "com.lihaoyi" %%% "fansi" % "0.4.0",
  "com.lihaoyi" %%% "os-lib" % "0.9.1",
  "com.monovore" %%% "decline" % "2.4.1",
  "io.argonaut" %%% "argonaut" % "6.3.8",
  "org.scalatest" %% "scalatest" % "3.2.16" % Test
)

enablePlugins(ScalaNativePlugin)
```

## handle i/o
To get a file path from command line argument, we use oslib.
First, we check if file exists. Second, check the extension of the file. Third, if file is in csv or json format, we read and parse the content. 

```scala
import os._
val path = os.pwd / cmd.fileName
if(os.isFile(path)) {
  path.ext match {
    case "csv" => os.read.lines.stream(path).foreach { line =>
       ???
     }
    case "json" => ???
    case other => UnsupportedExt(other).asLeft
  }
}else {
  Left(IsNotFile())
}
```
## parse command line arguments with decline

Decline offers composable syntax to parse command line arguments.

For example, this command below parses an argument like `<command-name> example.txt` as `Path`.

```scala
val file = Opts.argument[Path](metavar="file")
val cmd = Command.apply("command-name","header text",true)(file)
```

`Opts` has other constructors such as `Opts.arguments`(accept multiple arguments), `Opts.flag`(accept single flag starting with `--`) and `Opts.option(...)`(accept a flag and its argument of type T like `--optionname value`).

```scala
val fooFlag = Opts.flag("flagname",help="description")
// Opts[Unit] = Opts(--flagname)

val intOption = Opts.option[Int](long="meaningoflife",short="m",metavar="meaning of life","???")
val cmd = Command.apply("...","...",true)(intOption)

cmd.parse(Seq("--meaningoflife","42")) // or cmd.parse(Seq("-m42"))
// Right(42)
```

For more details, read decline documentation.
## decorate command-line output

You can add decoration to terminal output such as emphasis, underline, bold, and color using fansi.

## parse json

It is easier to display array of json objects as a table than to display arbitrary json. Thus, our application accepts only json array. 

To handle json string in a type-safe way, we use argonaut and its json types.
```scala
import argonaut._


val json = Parse.parse(rawValue)
json match {
  case Right(j) if j.isArray =>
    ???
  case Right(_) =>
    Left(JsonIsNotArray())
  case _ => Left(ParseError())
}
```


Now, start writing the application and execute `sbt run` command to build and run your application.

For more details, `see src/main/scala/*.scala` .


## Useful links
- https://scala-native.readthedocs.io/en/latest/
- https://ben.kirw.in/decline/
- https://github.com/com-lihaoyi/os-lib
- https://github.com/com-lihaoyi/fansi
- scala-native documentation : http://scala-native.org/
- Write CLI Application with Scala and graal native image : https://msitko.pl/blog/2020/03/10/writing-native-cli-applications-in-scala-with-graalvm.html
