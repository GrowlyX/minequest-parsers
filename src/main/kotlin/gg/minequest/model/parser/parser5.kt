package gg.minequest.model.parser

import org.json.JSONObject
import java.io.File

/**
 * @author GrowlyX
 * @since 8/23/2022
 */

fun main(args: Array<String>)
{
    val vals = File("bruhh1.json")
        .readLines()

    val file = File("diamond_pickaxe.json")

    val jsonObject = JSONObject(file.readText())

    jsonObject.getJSONArray("overrides")
        .forEachIndexed { index, thing ->
            val thingie = vals.getOrNull(index)
                ?: return@forEachIndexed

            val `object` = thing as JSONObject
            `object`.getJSONObject("predicate")
                .put("damage", thingie.toDouble())
        }

    File("newthingie.json")
        .apply {
            createNewFile()
            appendText(jsonObject.toString(1))
        }

}
