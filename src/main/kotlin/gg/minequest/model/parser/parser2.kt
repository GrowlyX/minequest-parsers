package gg.minequest.model.parser

import org.json.JSONObject
import java.io.File

/**
 * @author GrowlyX
 * @since 8/23/2022
 */

fun main(args: Array<String>)
{
    var initialDamage = args.getOrNull(0)
        ?.toIntOrNull() ?: 37

    val texturePrefix = args.getOrNull(1)
        ?: "blockmodels/picnic/textures/"

    println("Welcome to the thingie thingifier")

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

    val mappings = mutableListOf<String>()
    val mappingsFile = File("mappings.txt")
        .apply {
            if (!this.exists())
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

                val newFileProps = File(
                    modelsOut, "${it.nameWithoutExtension}.properties"
                )
                newFileProps.createNewFile()

                val current = initialDamage++

                val properties = """
                    type=item
                    items=271
                    model=${it.nameWithoutExtension}.json
                    damage=$current
                """.trimIndent()

                runCatching {
                    newFileProps.writeText(properties)
                    mappings += "file ${it.name} - $current"
                }.onFailure {
                    failures += "(!) FAILED to SAVE script ${newFileProps.name} (${it.message})"
                }.onSuccess {
                    pokemon++
                    println("Wrote edited script to ${newFileProps.name}")
                }

                // thingies

                val text = it.readText()
                val jsonObject = JSONObject(text)

                val keys = jsonObject
                    .getJSONObject("textures")
                    .keySet()

                val textures = jsonObject
                    .getJSONObject("textures")

                for (key in keys)
                {
                    textures.put(
                        key, "$texturePrefix${
                            textures.getString(key).split("/").last()
                        }"
                    )
                }

                pokemon += 1

                val newFile = File(modelsOut, it.name)
                newFile.createNewFile()

                jsonObject
                    .getJSONObject("display")
                    .put(
                        "thirdperson",
                        mapOf(
                            "rotation" to listOf(
                                15, 0, -190
                            ),
                            "translation" to listOf(
                                3.75, 3, 1.1895
                            ),
                            "scale" to listOf(
                                1, 1, 1
                            )
                        )
                    )

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

    println("(!!!!) Successfully parsed $pokemon THINGIES")

    if (failures.isNotEmpty())
    {
        failuresFile.writeText(
            failures.joinToString("\n")
        )
    }

    mappingsFile.appendText(
        mappings.joinToString("\n")
    )
}
