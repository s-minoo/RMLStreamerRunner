package RMLInjector

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.vocabulary.RDF

import java.beans.Statement
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Paths

import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption
import java.io.Writer
import RMLInjector.IOType
import RMLInjector.vocabulary._

case class RMLHandler(val baseURI: String) {

  def parse(fileName: String): Model = {
    val reader = Files.newBufferedReader(Paths.get(fileName))
    parse(reader)
  }

  def parse(reader: Reader): Model = {
    ModelFactory.createDefaultModel().read(reader, baseURI, "TTL")
  }

  private def updateSource(
      ioType: IOType,
      node: Resource
  ): Unit = {}


  /** Updates the given RML model with the given I/O type and write it using the writer
    * 
    * This function has a side-effect of modifying the RML model in-place. Becareful 
    * if you need the model to be immutable. 
    * 
    * Currently only supports changing of logical sources with the given [[inputType]].
    * TODO: also support output (logical targets) 
    *
    * @param model Jena model representing the RML document
    * @param inputType The input logical source type. See [[RMLRunner.IOType]]
    * @param outputType The output logical target type. 
    * @param writer Writer to be used to serialize the updated RML Jena model 
    */
  def updateModel(
      model: Model,
      inputType: IOType,
      writer: Writer,
      mapHostName: String => String,
  ): Unit = {
    val sourceProperty = model.createProperty(RMLVoc.Property.SOURCE)

    val logicalSources = model
      .listSubjectsWithProperty(
        sourceProperty
      )
      .toList()



    // Remove the old rml:source predicates from the mapping file
    logicalSources.forEach(s => {
      val sourceBNode = model.getProperty(s, sourceProperty)
      model.removeAll(s, sourceProperty, null)
      sourceBNode.remove()
    })

    // and add the new rml:source with the given inputType
    logicalSources
      .forEach(s => {
        inputType match {
          case TcpIO(hostport) =>{
            val sourceBNode = model.createResource()
            s.addProperty(sourceProperty, sourceBNode) 
            val Array(hostname, port) = hostport.split(":")
            sourceBNode.addProperty(model.createProperty(RMLSVoc.Property.HOSTNAME),
            hostname
            )
            sourceBNode.addProperty(model.createProperty(RMLSVoc.Property.PORT),
            port
            )
          }
          case FileIO(fileName) => {
            s.addProperty(sourceProperty, fileName)
          }
          case KafkaIO(hostIp, topic, groupId) => {
            val sourceBNode = model.createResource()
            s.addProperty(sourceProperty, sourceBNode)
            sourceBNode.addProperty(
              RDF.`type`,
              model.createResource(RMLSVoc.Class.KAFKA_STREAM)
            )
            sourceBNode.addProperty(
              model.createProperty(RMLSVoc.Property.BROKER),
              mapHostName(hostIp.head)
            )
            sourceBNode.addProperty(
              model.createProperty(RMLSVoc.Property.GROUPID),
              groupId.get
            )
            sourceBNode.addProperty(
              model.createProperty(RMLSVoc.Property.TOPIC),
              topic
            )
          }
          case StdIO() =>
            throw new IllegalArgumentException("StdIO is not supported yet")

        }
      })

    model.write(
      writer, 
      "TTL"
    )

  }

}

object RMLHandler {

  def apply(baseURI: Option[String] = None): RMLHandler = {
    new RMLHandler(baseURI.getOrElse("http://rmlrunner.example.com"))
  }
}
