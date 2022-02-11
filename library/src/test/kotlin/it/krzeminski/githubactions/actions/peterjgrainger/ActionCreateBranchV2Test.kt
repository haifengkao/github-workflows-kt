package it.krzeminski.githubactions.actions.peterjgrainger

import io.kotest.core.spec.style.DescribeSpec
import it.krzeminski.githubactions.shouldHaveYamlArguments

class ActionCreateBranchV2Test : DescribeSpec({
    it("renders with defaults") {
        ActionCreateBranchV2() shouldHaveYamlArguments emptyMap()
    }

    it("renders with all parameters") {
        ActionCreateBranchV2(
            branch = "main",
            sha = "ec955710c66016d511585121e092b27066a99c28",
        ) shouldHaveYamlArguments mapOf(
            "branch" to "main",
            "sha" to "ec955710c66016d511585121e092b27066a99c28",
        )
    }
})