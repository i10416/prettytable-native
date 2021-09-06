
# Write a Simple CLI Application with Scala Native

## Goal
Our goal is to write a toy application which pretty-prints a csv file in your terminal.

Input: example.csv
```
id,name,icon,comment,favorite
1,tama,😼,meow,matatabi
2,pochi,🐶,bowwow,🦴
```
Output
```
prettytable example.csv
id|name |icon|comment|favorite
==+=====+====+=======+========
1 |tama |😼  |meow   |matatabi
--+-----+----+-------+--------
2 |pochi|🐶  |bowwow |🦴
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

We use following dependencies to build the application.

- cats: It offers functional data structures and syntax.
- decline: It enables us to handle command-line arguments in a functional way.
- oslib: simple and unopinionated i/o utilities.
- fansi: decorate terminal output.

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
scalaVersion := "2.13.6"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

libraryDependencies ++=Seq(
        "org.typelevel" %%% "cats-core" % "2.6.1",
        "com.lihaoyi"  %%% "fansi"        % "0.2.10",
        "com.lihaoyi"  %%% "os-lib"       % "0.7.2",
        "com.monovore"  %%% "decline" % "2.1.0"
  )

enablePlugins(ScalaNativePlugin)
```

For details, `see src/main/scala/*.scala` .
