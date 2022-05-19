package RMLInjector

import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import java.nio.file.Paths

@RunWith(classOf[JUnitRunner])
class IntegrationTest extends AnyFlatSpec {

  def getJsonConfig() = {
    Paths
      .get(getClass().getResource("/config_test.json").getPath())
      .toFile()
  }

  "Runner config" should "be parsed without error" in {
    val file = getJsonConfig()
    assert(file.exists(), s"File doesn't exists: ${file}")
    val runnerConfig = RunnerCLIConfig(file)
    val cli_args = App.handleRunnerCLI(runnerConfig).split(" ").patch(2, Nil, 1).toSeq

    val expected = Seq("--bulk", "-m", "toKafka", "-b", "localhost:9092", "-t", "epicTopic")


    assert(expected.equals(cli_args), s"Expected: ${expected}, Output: ${cli_args}")
    

  }

}
