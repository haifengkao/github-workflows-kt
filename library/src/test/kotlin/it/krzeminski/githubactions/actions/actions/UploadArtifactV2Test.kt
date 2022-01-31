package it.krzeminski.githubactions.actions.actions

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class UploadArtifactV2Test : DescribeSpec({
    it("renders with defaults") {
        // given
        val action = UploadArtifactV2(
            name = "CoolArtifact",
            path = listOf(
                "/some/path",
                "/another/path",
            )
        )

        // when
        val yaml = action.toYamlArguments()

        // then
        yaml shouldBe linkedMapOf(
            "name" to "CoolArtifact",
            "path" to "/some/path\n/another/path",
        )
    }
})