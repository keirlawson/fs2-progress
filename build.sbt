ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "io.github.keirlawson"

lazy val root = (project in file("."))
  .settings(
    name := "fs2-progress",
    libraryDependencies ++= Seq(
      "me.tongfei" % "progressbar" % "0.9.3",
      "co.fs2" %% "fs2-core" % "3.2.7"
    )
  )
