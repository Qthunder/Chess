name := "Chess"

version := "0.1"

scalaVersion := "2.13.4"

scalacOptions ++=
  List(
    "-opt:inline",
    "-opt:l:inline",
    "-opt-warnings:at-inline-failed-summary",
    "-opt-inline-from:**"
  )

libraryDependencies ++=
  List(
    "org.typelevel" %% "cats-core" % "2.3.0",
  )
