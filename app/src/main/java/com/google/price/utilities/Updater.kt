package com.google.price.utilities

import org.jsoup.Jsoup
import java.lang.Exception
import kotlin.text.Regex

class Updater {
    companion object {
        private var config = arrayOf(
                ConfigObject("aliexpress", "aliexpress.com", "div.product-price-current span.product-price-value"),
                ConfigObject("ebay", "ebay", ".notranslate"),
                ConfigObject("taobao", "taobao.com", "#J_PromoPriceNum")
        )

        @JvmStatic
        fun update(array: ArrayList<String>): ArrayList<Float> {
            var result = ArrayList<Float>()
            for (element in array) run {
                var currentSite: ConfigObject? =
                    config.find { configObject -> element.contains(configObject.link, ignoreCase = true) } ?: return@run
                Jsoup.connect(element).get().run {
                    // First select price
                    var r = Regex("[0-9]+(\\.[0-9]*)*")
                    try {
                        var price: Float =  r.find(select(currentSite!!.selector)[0].text())!!.value.toFloat()
                        result.add(price)
                    } catch (e: Exception) {
                        result.add((-1.0).toFloat())
                    }
                }
            }
            return result
        }
    }
    private data class ConfigObject(val name: String, val link: String, val selector: String)
}
