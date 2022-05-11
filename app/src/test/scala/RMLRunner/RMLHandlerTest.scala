package RMLRunner

import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.flatspec.AnyFlatSpec
import java.io.File
import scala.io.Source
import java.io.BufferedReader
import RMLRunner.vocabulary.RMLVoc
import io.rml.framework.core.vocabulary.R2RMLVoc
import org.apache.jena.rdf.model.Model

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

}
