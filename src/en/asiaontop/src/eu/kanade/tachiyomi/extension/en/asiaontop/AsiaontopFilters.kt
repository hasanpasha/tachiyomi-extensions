package eu.kanade.tachiyomi.extension.en.asiaontop

import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList

class AsiaontopFilters() {

    companion object {

        fun getFilterList() = FilterList(
            FilterByFilter(),
            Filter.Separator(),
            ByStudioFilter(),
            ByCountryFilter(),
            ByFetishFilter(),
            ByTagFilter(),
        )

        fun buildUrl(baseUrl: String, page: Int, filter: FilterList): String {
            val filterByFilter = filter.findInstance<FilterByFilter>()!!
            val byStudioFilter = filter.findInstance<ByStudioFilter>()!!
            val byCountryFilter = filter.findInstance<ByCountryFilter>()!!
            val byFetishFilter = filter.findInstance<ByFetishFilter>()!!
            val byTagFilter = filter.findInstance<ByTagFilter>()!!

            val filterState = filterByFilter.state
            if (filterState != 0) {
                val url = "${baseUrl}${filterByList[filterState].second}"

                if (filterState == 1) {
                    return "$url/${byStudioList[byStudioFilter.state].second}"
                } else if (filterState == 2) {
                    return "$url/${byCountryList[byCountryFilter.state].second}"
                } else if (filterState == 3) {
                    return "$url/${byFetishList[byFetishFilter.state].second}"
                } else if (filterState == 5) {
                    return "$baseUrl/tag/${byTagFilter.state.replace(' ', '-')}"
                } else {
                    return url
                }
            } else {
                return baseUrl
            }
        }

        private val filterByList = arrayOf<Pair<String, String>>(
            Pair("", ""),
            Pair("By Studio", "/category/by-studio"),
            Pair("By Country", "/category/by-country"),
            Pair("Fetish", "/category/fetish"),
            Pair("Nude", "/category/nude"),
            Pair("By Tags", "/find-sexy-girl-by-tags"),
        )

        private class FilterByFilter(
            values: Array<Pair<String, String>> = filterByList,
        ) : Filter.Select<String>("Filters", values.map { it.first }.toTypedArray())

        private val byStudioList = arrayOf(
            Pair("rtGr@vi", "rtgrvi"),
            Pair("Beautyleg", "beautyleg"),
            Pair("BlueCake", "bluecake"),
            Pair("BoLoli", "bololi"),
            Pair("Candy", "candy"),
            Pair("Coser", "coser"),
            Pair("CreamSoda", "creamsoda"),
            Pair("DJAWA", "djawa"),
            Pair("DKGirl", "dkgirl"),
            Pair("FeiLin", "feilin"),
            Pair("GIRLT", "girlt"),
            Pair("GIRL – SMOU", "girl-smou"),
            Pair("Goddess", "goddess"),
            Pair("Graphis", "graphis"),
            Pair("HuaYan", "huayan"),
            Pair("HuaYang", "huayang"),
            Pair("IMiss", "imiss"),
            Pair("JOApictures", "joapictures"),
            Pair("Kimoe", "kimoe"),
            Pair("LeYuan", "leyuan"),
            Pair("LiGui丽柜", "ligui%e4%b8%bd%e6%9f%9c"),
            Pair("Loozy", "loozy"),
            Pair("MFStar", "mfstar"),
            Pair("MiCat – RuiSG", "micat"),
            Pair("MiStar", "mistar"),
            Pair("MTCos", "mtcos"),
            Pair("MiiTao", "miitao"),
            Pair("MTCos", "mtcos"),
            Pair("MyGirl", "mygirl"),
            Pair("OnlyFans", "onlyfans"),
            Pair("TASTE顽味生活", "taste%e9%a1%bd%e5%91%b3%e7%94%9f%e6%b4%bb"),
            Pair("TGOD", "tgod"),
            Pair("TouTiao", "toutiao"),
            Pair("TuiGirl", "tuigirl"),
            Pair("SAINT Photolife", "saint-photolife"),
            Pair("YaLaYi", "yalayi"),
            Pair("YOUMI", "youmi"),
            Pair("YouWu", "youwu"),
            Pair("Young Animal", "young-animal"),
            Pair("QuingDouKe", "quingdouke"),
            Pair("UGirls", "ugirls"),
            Pair("Ugirls App", "ugirls-app"),
            Pair("UXing", "uxing"),
            Pair("XiaoYu", "xiaoyu"),
            Pair("Xiuren (秀人网)", "xiuren"),
            Pair("XINGYAN", "xingyan"),
        )

        private class ByStudioFilter(
            values: Array<Pair<String, String>> = byStudioList,
        ) : Filter.Select<String>("By Studio", values.map { it.first }.toTypedArray())

        private val byCountryList = arrayOf(
            Pair("Korean", "korean"),
            Pair("Japanese", "japanese"),
            Pair("Vietnamese", "vietnamese"),
            Pair("Western", "western"),
        )

        private class ByCountryFilter(
            values: Array<Pair<String, String>> = byCountryList,
        ) : Filter.Select<String>("By Country", values.map { it.first }.toTypedArray())

        private val byFetishList = arrayOf(
            Pair("AISS", "aiss"),
            Pair("ROSI", "rosi"),
            Pair("EROONICHAN", "eroonichan"),
            Pair("MASKED QUEEN", "masked-queen"),
        )

        private class ByFetishFilter(
            values: Array<Pair<String, String>> = byFetishList,
        ) : Filter.Select<String>("By Fetish", values.map { it.first }.toTypedArray())

        private class ByTagFilter() : Filter.Text("By Tags")

        private inline fun <reified T> Iterable<*>.findInstance() = find { it is T } as? T
    }
}
