# GitHub Actions Kotlin DSL

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.krzeminski/github-actions-kotlin-dsl/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.krzeminski/github-actions-kotlin-dsl)
[![Slack channel](https://img.shields.io/badge/chat-slack-blue.svg?logo=slack)](https://kotlinlang.slack.com/messages/github-actions/)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

---

## 🧪 Work in progress - "moving fast" phase

Please expect breaking API changes (they're documented in release notes) and missing features.

---

## 💡 Idea

YAMLs and JSONs surround us more and more frequently. While their syntax is simple and they allow defining hierarchical
data easily, the tendency is also to overuse them for more complicated scenarios where a power of a regular programming
language would be beneficial. This library aims at filling this gap, utilizing Kotlin as a modern general-purpose
language with good internal DSL support.

## ✨ Benefits

* no more confusion about YAML's indent level - Kotlin's syntax doesn't rely on it
* thanks to Kotlin being a compiled language, adds a compilation phase where a number of errors can be caught and
  prevented from getting into your workflows' runtime phase
* superb IDE support: author your workflows in any IDE that supports Kotlin, with auto-completion
* remove duplication: ability to extract common parts to constants or functions
* programmatically generate your workflow's arbitrarily complex logic, you can even call an external service and
  generate your workflow based on the response. Whatever Kotlin and the JVM allows you to do

## 🛠️ Usage

As an exercise, we'll add a job that prints out `Hello world!`. Feel free to replace the actual workflow's logic and all
names with your own.

1. Install Kotlin as a stand-alone binary, e.g. from Snap Store when on Linux:
   ```
   sudo snap install kotlin --classic
   ```
2. Create a new executable file in your repository:
   ```
   touch    .github/workflows/hello_world_workflow.main.kts
   chmod +x .github/workflows/hello_world_workflow.main.kts
   ```
   This location is not a hard requirement, it's just recommended for consistency with enforced location of actual
   GitHub Actions workflows.
3. Put this content into the previously created file and save it:
   ```kotlin
   #!/usr/bin/env kotlin

   @file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.5.0")

   import it.krzeminski.githubactions.actions.actions.CheckoutV2
   import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
   import it.krzeminski.githubactions.domain.Trigger.Push
   import it.krzeminski.githubactions.dsl.workflow
   import it.krzeminski.githubactions.yaml.toYaml
   import java.nio.file.Paths

   val workflow = workflow(
       name = "Test workflow",
       on = listOf(Push()),
       sourceFile = Paths.get(".github/workflows/hello_world_workflow.main.kts"),
       targetFile = Paths.get(".github/workflows/hello_world_workflow.yml")
   ) {
       job(name = "test_job", runsOn = UbuntuLatest) {
           uses(name = "Check out", action = CheckoutV2())
           run(name = "Print greeting", command = "echo 'Hello world!'")
       }
   }

   println(workflow.toYaml())
   ```

   Explanation: first, we create a workflow with the DSL provided by this library. The reason it needs source and target
   file paths is to be able to generate consistency checks, to ensure that both source and target files are in sync.
   You'll see it in a moment in the generated file.What's written to the `workflow` variable is an object of type
   `it.krzeminski.githubactions.domain.Workflow`, it's not a YAML yet. However, a call to `toYaml()` extension function
   does the final piece of job.

   Alternatively, apart from `toYaml()` which returns a string, there's also `writeToFile()` which puts the string
   straight into the file specified in workflow's `targetFile`. It may come in handy when operating on multiple
   workflows and having automation that regenerates them. Note that this function doesn't provide consistency check.
4. Generate the YAML by calling the above script and redirecting its output to the desired YAML file path:
   ```
   .github/workflows/hello_world_workflow.main.kts > .github/workflows/hello_world_workflow.yml
   ```

   Notice that there's an extra job generated by the library that regenerates the YAML in job's runtime and ensures that
   it's equal to the YAML committed to the repository.
5. Commit both files, push the changes to GitHub and make sure the workflow is green when ran on GitHub Actions.

More examples can be found [here](https://github.com/krzema12/github-actions-kotlin-dsl/tree/main/examples/src/main/kotlin).

## ❓ Frequently Asked Questions

### What if I need an action that doesn't have a wrapper in this library?

Please first ensure that your action isn't present [here](library/src/main/kotlin/it/krzeminski/githubactions/actions).

Wrapping an action is as easy as inheriting after `it.krzeminski.githubactions.actions.Action`, which should contain a
piece of logic how to map an object of your class to YAML arguments:

```kotlin
class MyCoolActionV3(
    private val someArgument: String,
) : Action("acmecorp", "cool-action", "v3") {
    override fun toYamlArguments() = linkedMapOf(
        "some-argument" to someArgument,
    )
}
```

Then just use it in your workflow:
```kotlin
uses(name = "FooBar",
     action = MyCoolActionV3(someArgument = "foobar"))
```

After you have a working wrapper class, even partially, **please contribute it to the library** so that others can use
it!
