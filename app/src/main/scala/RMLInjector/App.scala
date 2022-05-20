/*
 * This Scala source file was generated by the Gradle 'init' task.
 */
package RMLInjector

import org.apache.jena.riot.RDFDataMgr
import scopt.OParser

import java.nio.file.Files
import java.nio.file.Path
import com.fasterxml.jackson.databind.ObjectMapper
import java.{util => ju}
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import sys.process._
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.io.File
import java.io.OutputStreamWriter
import java.io.FileWriter

case class RunnerCLIConfig(
    jsonConfigFile: File = new File(""),
    outputFile: String = "output_cli.txt"
)

case class RMLStreamerCLIConfig(
    mappingFile: String,
    jobName: Option[String] = None,
    baseIRI: Option[String] = None,
    disableParal: Boolean = false,
    bulk: Boolean = false,
    jsonLD: Boolean = false,
    output: IOType = StdIO()
)

object App {
  def buildRunnerCLIParser(): OParser[Unit, RunnerCLIConfig] = {

    val builder = OParser.builder[RunnerCLIConfig]

    val parser = {
      import builder._
      OParser.sequence(
        programName("RMLRunner"),
        head("RMLRunner"),
        opt[String]('c', "config")
          .required()
          .valueName("<filepath>")
          .validate(x =>
            if (Files.exists(Path.of(x))) success
            else failure(f"File doesn't exists")
          )
          .action((x, c) => c.copy(jsonConfigFile = Paths.get(x).toFile()))
          .text("A json config file for RMLRunner to configure RMLStreamer"),
        head("help").text(
          "A runner application for executing RMLStreamer using the config file given by the Orchestrator"
        ),
        opt[String]('o', "output")
          .optional()
          .valueName("<filepath>")
          .action((x, c) => c.copy(outputFile = x))
          .text(
            "The output file where the cli args needed to run RMLStreamer will be written to"
          )
      )
    }
    parser
  }

  def parseRMLCLI(node: JsonNode): RMLStreamerCLIConfig = {
    val output = IOType(node.get("args").get("outputStream").get(0))
    val mappingFile = node.get("args").get("rmlmapping").asText()
    val config = node.get("processorConfig").get("config")
    val bulk =
      Option(config.get("bulk")).map(_.asBoolean()).getOrElse(false)
    val jsonLD =
      Option(config.get("json-ld")).map(_.asBoolean()).getOrElse(false)
    val disableParal =
      Option(config.get("disable-parallelism"))
        .map(_.asBoolean())
        .getOrElse(false)

    val jobName =
      Option(config.get("jobName")).map(_.asText())
    val baseIRI =
      Option(config.get("baseIRI")).map(_.asText())

    RMLStreamerCLIConfig(
      mappingFile,
      jobName,
      baseIRI,
      disableParal,
      bulk,
      jsonLD,
      output
    )

  }

  def handleRunnerCLI(cli: RunnerCLIConfig) = {
    val objMapper = new ObjectMapper()



    val jsonTree = objMapper.readTree(cli.jsonConfigFile)
    val inputConfig = jsonTree.get("args").get("inputStream").get(0)
    val inputType = IOType(inputConfig)
    val rmlcliargs = parseRMLCLI(jsonTree)

    val mappingFile = Option(
        jsonTree
          .get("processorConfig")
          .get("location")
      )
      .map(_.asText() + "/")
      .getOrElse("") + rmlcliargs.mappingFile

    val handler = RMLHandler()
    val model = handler.parse(mappingFile)
    val tempFilePath = Files.createTempFile(null, "-mapping.ttl")
    val writer = Files.newBufferedWriter(
      tempFilePath,
      StandardOpenOption.TRUNCATE_EXISTING
    )

    handler.updateModel(model, inputType, writer)
    var args = Seq[String]()

    rmlcliargs.output match {
      case FileIO(fileName) => args ++= Seq("toFile", "-o", fileName)
      case KafkaIO(hostIp, topic, groupId) =>
        args ++= Seq("toKafka", "-b", hostIp.mkString(","), "-t", topic)
      case TcpIO(hostIp) =>
        args ++= Seq("toTCPSocket", "-s", hostIp)
      case StdIO() => ""
    }

    if (rmlcliargs.bulk) {
      args :+= "--bulk"
    }

    if (rmlcliargs.jsonLD) {
      args :+= "--json-ld"
    }

    if (rmlcliargs.disableParal) {
      args :+= "--disable-local-parallel"
    }

    val output_File = Paths.get(cli.outputFile).toFile()
    output_File.createNewFile()

    val output_writer = new FileWriter(output_File)
    output_writer.write(s"MAPPING_FILE=\"${tempFilePath.toString()}\"\n")
    output_writer.write(s"CLI_ARGS=\"${args.mkString(" ")}\"\n")
    output_writer.flush()
    output_writer.close()

    args.mkString(" ")

  }

  def main(args: Array[String]): Unit = {

    val parser = buildRunnerCLIParser()
    OParser.parse(parser, args, RunnerCLIConfig()) match {
      case Some(value) => handleRunnerCLI(value)
      case _           =>
    }

  }

}
