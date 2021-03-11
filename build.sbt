lazy val akkaHttpVersion = "10.2.4"
lazy val akkaVersion    = "2.6.13"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.13.4"
    )),
    name := "log-analyzer",
    libraryDependencies ++= Seq(
      "io.spray" %%  "spray-json" % "1.3.6",
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % "2.0.2",
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "org.xerial" % "sqlite-jdbc" % "3.34.0"
    )
  )
