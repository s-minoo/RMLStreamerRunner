package RMLInjector.vocabulary

object RMLSVoc {

  val namespace = ("rmls", "http://semweb.mmlab.be/ns/rmls#")

  object Property {
    ///////////////////////////////////////////////////////////////////////////
    // RMLS: TCP Source
    ///////////////////////////////////////////////////////////////////////////

    val HOSTNAME = namespace._2 + "hostName";
    val PORT = namespace._2 + "port";
    val PATH = namespace._2 + "path";
    val TYPE = namespace._2 + "type";

    ///////////////////////////////////////////////////////////////////////////
    // RMLS: Kafka Source
    ///////////////////////////////////////////////////////////////////////////

    val BROKER = namespace._2 + "broker";
    val GROUPID = namespace._2 + "groupId";
    val TOPIC = namespace._2 + "topic";
    val KAFKAVERSION = namespace._2 + "kafkaVersion";


  }

  object Class {

    val KAFKA_STREAM = namespace._2 + "KafkaStream"
    val TCP_STREAM = namespace._2 + "TcpStream"
  }

  def apply(suffix:String):String = {
    namespace._2 +  suffix 
  }
}
