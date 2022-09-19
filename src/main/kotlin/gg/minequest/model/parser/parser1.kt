package gg.minequest.model.parser

import org.json.JSONObject
import java.io.File

/**
 * @author GrowlyX
 * @since 8/23/2022
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

                val text = it.readText()
                val jsonObject = JSONObject(text)

                val keySetFirst = jsonObject.getJSONObject("textures")
                    .keySet().firstOrNull()

                if (
                    jsonObject.getJSONObject("textures")
                        .getString(keySetFirst ?: "0")
                        ?.startsWith("pokemon/") == true
                )
                {
                    println("This is a pokemon, ${it.name}")
                    return@forEach
                }

                pokemon += 1

                val newFile = File(modelsOut, it.name)
                newFile.createNewFile()

                runCatching {
                    newFile.writeText(
                        jsonObject.toString(4)
                    )
                }.onFailure {
                    failures += "(!) FAILED to SAVE script ${newFile.name} (${it.message})"
                }.onSuccess {
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
