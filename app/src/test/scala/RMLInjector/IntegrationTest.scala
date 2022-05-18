package RMLInjector

import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import java.nio.file.Paths

@RunWith(classOf[JUnitRunner])
class IntegrationTest extends AnyFlatSpec{

    def getJsonConfig() = {
        Paths.get(getClass().getResource("/runner_config_test.json").toString()).toFile()

    }



    "Runner config" should "be parsed without error" in {
       val runnerConfig =  RunnerCLIConfig(getJsonConfig())
       App.handleRunnerCLI(runnerConfig)


    } 
  
}
