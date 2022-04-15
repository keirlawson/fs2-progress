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

publishTo := sonatypePublishToBundle.value
publishMavenStyle := true
sonatypeProfileName := "io.github.keirlawson"
sonatypeCredentialHost := "s01.oss.sonatype.org"

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/keirlawson/fs2-progress"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/keirlawson/fs2-progress"),
    "scm:git@github.com:keirlawson/fs2-progress.git"
  )
)
developers := List(
  Developer(id="keirlawson", name="Keir Lawson", email="keirlawson@gmail.com", url=url("https://github.com/keirlawson/"))
)

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
) 