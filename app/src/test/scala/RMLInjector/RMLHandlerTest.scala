package RMLInjector

import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.flatspec.AnyFlatSpec
import java.io.File
import scala.io.Source
import java.io.BufferedReader
import org.apache.jena.rdf.model.Model
import java.io.Writer
import java.io.OutputStreamWriter
import RMLInjector.vocabulary.RMLVoc
import RMLInjector.vocabulary.R2RMLVoc

@RunWith(classOf[JUnitRunner])
class RMLHandlerTest extends AnyFlatSpec {

  def loadModel(): Model = {
    val bReader = Source.fromResource("mapping.ttl").bufferedReader()
    RMLHandler().parse(bReader)
  }

  "RMLHandler" should "parse the RMLMapping file correctly" in {
    loadModel()
    succeed
  }
  it should "contain logical sources" in {
    val parsedTMs = loadModel()
    assert(
      !parsedTMs
        .listStatements()
        .filterKeep(stmt =>
          stmt.getPredicate().toString().equals(RMLVoc.Property.LOGICALSOURCE)
        )
        .toList()
        .isEmpty()
    )
  }

  it should "contain triple maps" in {
    val parsedTMs = loadModel()
    assert(
      !parsedTMs
        .listStatements()
        .filterKeep(stmt =>
          stmt.getObject().toString().equals(R2RMLVoc.Class.TRIPLESMAP)
        )
        .toList()
        .isEmpty()
    )
  }

  it should "update the model with custom nodes" in {
    val parsedRMLModel = loadModel()
    val handler = RMLHandler()
    val input =
      KafkaIO(List("localhost:9000"), "epicTopic", Some("groupaUno"))
    val output = KafkaIO(List("localhost:9000"), "badoutput", None)

    handler.updateModel(parsedRMLModel, input, new OutputStreamWriter(System.out), x => x)
    val expectedModel = handler.parse(Source.fromResource("expected.ttl").bufferedReader())
    
    assert(expectedModel.isIsomorphicWith(parsedRMLModel))
  }

}
