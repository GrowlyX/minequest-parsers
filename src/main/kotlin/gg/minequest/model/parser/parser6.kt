package gg.minequest.model.parser

import org.json.JSONObject
import java.io.File

/**
 * @author GrowlyX
 * @since 8/23/2022
 */

fun main(args: Array<String>)
{
    File("models-in").walkTopDown()
        .forEach {
            if (it.isDirectory)
            {
                return@forEach
            }

            val thing = JSONObject(it.readText())
            thing.getJSONObject("display")
                .remove("thirdperson")

            it.writeText(thing.toString(1))
        }
}
