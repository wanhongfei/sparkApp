name := "Simple Project"
version := "1.0"
scalaVersion := "2.11.8"
libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.0.2" % "provided",
  "org.apache.spark" % "spark-streaming_2.11" % "2.0.2" % "provided",
  "org.apache.spark" % "spark-sql_2.11" % "1.6.2",
  "org.apache.spark" % "spark-streaming-kafka_2.11" % "1.6.2"
)
resolvers += "nexus-aliyun" at "http://maven.aliyun.com/nexus/content/groups/public"