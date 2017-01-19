name := "Simple Project"
version := "1.0"
scalaVersion := "2.10.6"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.2" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.0.2" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.6.2",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.6.2",
  "org.apache.kafka" % "kafka_2.10" % "0.10.1.0",
  "org.apache.kafka" % "kafka-clients" % "0.10.1.0",
  "org.scala-lang" % "scala-library" % "2.10.6",
  "org.scala-lang" % "scala-reflect" % "2.10.6",
  "org.scala-lang" % "scala-compiler" % "2.10.6",
  "org.scala-lang" % "scalap" % "2.10.6",
  "com.yammer.metrics" % "metrics-annotation" % "2.2.0",
  "com.yammer.metrics" % "metrics-core" % "2.2.0",
  "com.101tec" % "zkclient" % "0.10"
)
resolvers += "nexus-aliyun" at "http://maven.aliyun.com/nexus/content/groups/public"