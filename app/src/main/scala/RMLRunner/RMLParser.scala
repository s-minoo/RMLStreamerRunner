package RMLRunner

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFDataMgr

case class RMLParser(val baseURI: String) {



    def parse ( fileName: String): Model = {
        val model = ModelFactory.createDefaultModel(); 

        val is =  RDFDataMgr.open(fileName); 

        model.read(is, baseURI)
    }
  
}




object  RMLParser{

    def apply(baseURI: Option[String] = None): RMLParser ={
        new RMLParser(baseURI.getOrElse("http://rmlrunner.example.com")) 
    }
}
