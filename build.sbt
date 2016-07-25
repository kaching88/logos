lazy val commonSettings = Seq(
  organization := "com.github.kaching88",

  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-unchecked"
  ),

  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),

  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.sonatypeRepo("snapshots"),

  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  autoCompilerPlugins := true,

  fork in Test := true,
  parallelExecution in Test := false ,

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  pomExtra :=
    <url>https://github.com/kaching88/logos</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/kaching88/logos</url>
      <connection>https://github.com/kaching88/logos</connection>
    </scm>
    <developers>
      <developer>
        <id>kaching88</id>
        <name>Bertrand Wlodarczyk</name>
        <url>https://github.com/kaching88</url>
      </developer>
    </developers>,

  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
)

lazy val coreDependencies = libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "uk.org.lidalia" % "slf4j-test" % "1.2.0" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

lazy val macroDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"
  ),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
      case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq()
      // in Scala 2.10, quasiquotes are provided by macro paradise
      case Some((2, 10)) =>
        Seq(
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
          "org.scalamacros" %% "quasiquotes" % "2.1.0" cross CrossVersion.binary
        )
    }
  }
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .aggregate(core)

lazy val core = project.in(file("core"))
  .settings(commonSettings ++ coreDependencies ++ macroDependencies)
  .settings(
    moduleName := "logos",
    libraryDependencies += "org.typelevel" %% "macro-compat" % "1.1.1"
  )