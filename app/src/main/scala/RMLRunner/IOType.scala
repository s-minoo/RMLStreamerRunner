package RMLRunner

import com.fasterxml.jackson.databind.JsonNode
import scala.jdk.CollectionConverters._

sealed trait IOType

case class NullIO() extends IOType
case class KafkaIO(
    hostIp: List[String],
    topic: String,
    groupId: Option[String],
    partitionId: Option[String]
) extends IOType

case class FileIO(fileName: String) extends IOType

object IOType {

  def parseKafka(config: JsonNode): IOType = {
    val hostIps = config
      .get("broker")
      .get("hosts")
      .elements()
      .asScala
      .map(node => node.asText())
      .toList
    val topic = config.get("topic").get("name").asText()

    val groupId = Option(config.get("consumer")).map(_.get("groupId").asText())
    val partitionId = Option(config.get("producer")).map(_.get("partitionId").asText())

    KafkaIO(hostIps, topic, groupId, partitionId)
  }

  def apply(opt: JsonNode): IOType = {
    val typ = opt.get("type").asText()
    val config = opt.get("config")

    typ match {
      case "kafka" => parseKafka(config)
      case "file" => FileIO(config.get("filename").asText())
    }
  }
}
