name := "Simple Project"
version := "1.0"
scalaVersion := "2.11.8"
libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % "2.0.2" % "provided",
  "org.apache.spark" % "spark-streaming_2.10" % "2.0.2" % "provided",
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.2",
  "org.apache.kafka" % "kafka_2.10" % "0.10.0.0"
)
resolvers += "nexus-aliyun" at "http://maven.aliyun.com/nexus/content/groups/public"