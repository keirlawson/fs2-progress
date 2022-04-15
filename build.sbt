val scala213 = "2.13.8"
val scala3 = "3.1.2"

ThisBuild / scalaVersion     := scala213
ThisBuild / versionScheme    := Some("early-semver")
ThisBuild / organization     := "io.github.keirlawson"

lazy val root = (project in file("."))
  .settings(
    name := "fs2-progress",
    libraryDependencies ++= Seq(
      "me.tongfei" % "progressbar" % "0.9.3",
      "co.fs2" %% "fs2-core" % "3.2.7"
    ),
    crossScalaVersions := List(scala213, scala3),
    releaseCrossBuild := true,
    git.remoteRepo := "git@github.com:keirlawson/fs2-progress.git",
  )
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(SiteScaladocPlugin)

publishTo := sonatypePublishToBundle.value
publishMavenStyle := true
sonatypeProfileName := "io.github.keirlawson"
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

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