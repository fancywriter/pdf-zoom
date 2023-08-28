ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.0"
ThisBuild / Keys.scalacOptions := Seq("-feature", "-deprecation")

lazy val root = (project in file("."))
  .settings(
    name := "pdf-zoom",
    maintainer := "noreply@github.com",
    libraryDependencies ++= Seq(
      "com.github.librepdf" % "openpdf" % "1.3.30",
      "com.github.scopt" %% "scopt" % "4.1.0"
    )
  )

enablePlugins(JavaAppPackaging)
