package gg.minequest.model.parser

import org.json.JSONObject
import java.io.File

/**
 * @author GrowlyX
 * @since 8/23/2022
 */

fun main()
{
    File("cit")
        .walkTopDown()
        .forEach {
            if (it.isDirectory)
            {
                it.walkTopDown()
                    .forEach { file ->
                        runCatching {
                            if (file.name.endsWith("json"))
                            {
                                val text = file.readText()
                                val jsonObject = JSONObject(text)

                                val keys = jsonObject
                                    .getJSONObject("textures")
                                    .keySet()

                                val textures = jsonObject
                                    .getJSONObject("textures")

                                var changed = false

                                for (key in keys)
                                {
                                    val value = textures.getString(key)

                                    if (value.contains(":"))
                                    {
                                        val thingy = value
                                            .split(":").last()

                                        textures.put(
                                            key, "blockmodels/${
                                                file.parentFile.name
                                            }/textures/${thingy}"
                                        )

                                        changed = true
                                    }
                                }

                                if (changed)
                                {
                                    file.writeText(jsonObject.toString(4))

                                    println("updated this mf, ${file.name} in ${file.parentFile.name}")
                                }
                            }
                        }.onFailure { throwable ->
                            println("[FAILURE] ${throwable.message} for ${file.name}")
                        }
                    }
            }
        }

}
