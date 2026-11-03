package vn.kalapa.ads.sample.data

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioFixturesTest {
    @Test
    fun everyScenarioFixtureIsBundledInAssets() {
        val missing = Scenario.entries.mapNotNull { it.fixture }
            .filterNot { File("src/main/assets/$it").isFile }

        assertTrue("Missing fixtures: $missing", missing.isEmpty())
    }
}
