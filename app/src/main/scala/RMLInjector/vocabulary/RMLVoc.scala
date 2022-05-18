package RMLInjector.vocabulary

object RMLVoc {
  val namespace = ("rml", "http://semweb.mmlab.be/ns/rml#");

  object Property {

    ///////////////////////////////////////////////////////////////////////////
    // RML
    ///////////////////////////////////////////////////////////////////////////
    val REFERENCE = namespace._2 + "reference";
    val LOGICALSOURCE = namespace._2 + "logicalSource";
    val ITERATOR = namespace._2 + "iterator";
    val REFERENCEFORMULATION = namespace._2 + "referenceFormulation";
    val SOURCE = namespace._2 + "source";
    val LOGICALTARGET = namespace._2 + "logicalTarget";
  }



  def apply(suffix:String):String = {
    namespace._2 +  suffix 
  }
}
