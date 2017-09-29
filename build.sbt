name := "ScaIA"

version := "0.3"

scalacOptions += "-deprecation"
scalacOptions += "-feature"
scalacOptions += "-Yrepl-sync"

javaOptions in run += "-Xms8G"
javaOptions in run += "-Xmx8G"

mainClass in (Compile, run) := Some("org.scaia.util.asia.IAProblemSolver")
mainClass in assembly := Some("org.scaia.util.asia.IAProblemSolver")

fork := true

cancelable in Global := true

resolvers += "Artifactory-UCL" at "http://artifactory.info.ucl.ac.be/artifactory/libs-snapshot-local/"
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.typesafe.akka" %% "akka-remote" % "2.4.17" ,
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

logBuffered in Test := false