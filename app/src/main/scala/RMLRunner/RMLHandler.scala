package RMLRunner

import io.rml.framework.core.vocabulary.R2RMLVoc
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

import vocabulary.RMLVoc
import RMLRunner.vocabulary.RMLSVoc
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption
import java.io.Writer

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

  def updateModel(
      model: Model,
      inputType: IOType,
      outputType: IOType,
      writer: Writer,
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
      model.removeAll(sourceBNode.getObject().asResource(), null, null)
    })

    // and add the new rml:source with the given inputType
    logicalSources
      .forEach(s => {
        inputType match {
          case FileIO(fileName) => {
            s.addProperty(sourceProperty, fileName)
          }
          case KafkaIO(hostIp, topic, groupId, partitionId) => {
            val sourceBNode = model.createResource()
            s.addProperty(sourceProperty, sourceBNode)
            sourceBNode.addProperty(
              RDF.`type`,
              model.createResource(RMLSVoc.Class.KAFKA_STREAM)
            )
            sourceBNode.addProperty(
              model.createProperty(RMLSVoc.Property.BROKER),
              hostIp.head
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
