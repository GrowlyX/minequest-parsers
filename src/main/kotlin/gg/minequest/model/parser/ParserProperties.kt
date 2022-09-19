package gg.minequest.model.parser

import java.io.File

/**
 * @author GrowlyX
 * @since 8/28/2022
 */
fun main(args: Array<String>)
{
    val modelsIn = File("models-in")
        .apply {
            if (!this.exists())
                mkdirs()
        }

    val modelsOut = File("models-out")
        .apply {
            this.deleteRecursively()

            if (!this.exists())
                mkdirs()
        }

    val failures = mutableListOf<String>()
    val failuresFile = File("failures.txt")
        .apply {
            if (this.exists())
                this.delete()

            this.createNewFile()
        }

    var pokemon = 0

    modelsIn.walkTopDown()
        .forEach {
            runCatching {
                if (it.isDirectory)
                {
                    println("Skipping as ${it.name} is a directory")
                    return@forEach
                }

                val nameSplit = it
                    .nameWithoutExtension.split("_")

                val newFile = File(modelsOut, "${it.nameWithoutExtension}.properties")
                newFile.createNewFile()

                val properties = """
                    type=item
                    items=293
                    model=${it.name}
                    damage=${nameSplit[0]}
                """.trimIndent()

                runCatching {
                    newFile.writeText(properties)
                }.onFailure {
                    failures += "(!) FAILED to SAVE script ${newFile.name} (${it.message})"
                }.onSuccess {
                    pokemon++
                    println("Wrote edited script to ${newFile.name}")
                }
            }.onFailure { throwable ->
                failures += "(!) FAILED to EDIT script ${it.name} (${throwable.message})"
            }
        }

    println("(!!!!) Successfully parsed $pokemon pokémon")

    if (failures.isNotEmpty())
    {
        failuresFile.writeText(
            failures.joinToString("\n")
        )
    }
}
