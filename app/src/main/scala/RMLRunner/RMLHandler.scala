package RMLRunner

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFDataMgr

import vocabulary.RMLVoc
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Paths

case class RMLHandler(val baseURI: String) {

  def parse(fileName: String): Model = {
    val reader = Files.newBufferedReader(Paths.get(fileName))
    parse(reader)
  }

  def parse(reader: Reader):Model = {
    ModelFactory.createDefaultModel().read(reader, baseURI, "TTL")
  }


  def updateModel(
      model: Model,
      inputConfig: IOType,
      outputConfig: IOType
  ): Unit = {


    val logicalSource =  model.getProperty(RMLVoc.Property.LOGICALSOURCE).asResource()
    
  }



}

object RMLHandler {

  def apply(baseURI: Option[String] = None): RMLHandler = {
    new RMLHandler(baseURI.getOrElse("http://rmlrunner.example.com"))
  }
}
