package io.github.typesafegithub.workflows.actionbindinggenerator.generation

import io.github.typesafegithub.workflows.actionbindinggenerator.annotations.ExperimentalClientSideBindings
import io.github.typesafegithub.workflows.actionbindinggenerator.domain.NewestForVersion
import io.github.typesafegithub.workflows.shared.internal.findGitRoot
import java.nio.file.Path
import kotlin.io.path.div

/**
 * Implements the suggested logic of a script that is called to regenerate action bindings.
 *
 * @param args Whatever was passed to the script. The exact inputs are opaque and for now are an implementation detail
 * of the library, i.e. whatever YAML was generated by the library, it will produce correct input to the script and thus
 * this function.
 * @param sourceFile Path to the script where this function is called from. Typically a result of an expression like
 * [__FILE__.toPath()].
 */
@ExperimentalClientSideBindings
public fun generateActionBindings(
    args: Array<String>,
    sourceFile: Path,
) {
    val workflowYamlFileName: String? =
        when {
            args.isEmpty() -> null
            args.size == 1 -> args[0]
            else -> error("At most one argument is supported!")
        }

    val githubWorkflowsDir = sourceFile.findGitRoot() / ".github" / "workflows"

    val allUsedActions =
        extractUsedActionsFromWorkflow(
            manifest = (githubWorkflowsDir / "_used-actions.yaml").toFile().readText(),
        )

    val actionsToGenerateBindingsFor =
        workflowYamlFileName?.let {
            val actionsUsedInRequestedWorkflow =
                extractUsedActionsFromWorkflow(
                    manifest = (githubWorkflowsDir / workflowYamlFileName).toFile().readText(),
                )
            allUsedActions.intersect(actionsUsedInRequestedWorkflow.toSet())
        } ?: allUsedActions

    actionsToGenerateBindingsFor.forEach { action ->
        (githubWorkflowsDir / "generated" / action.owner / "${action.name}.kt").let {
            it.parent.toFile().mkdirs()
            val binding =
                action.generateBinding(
                    metadataRevision = NewestForVersion,
                    clientType = ClientType.CLIENT_SIDE_GENERATION,
                )
            it.toFile().writeText(binding.kotlinCode)
        }
    }
}
