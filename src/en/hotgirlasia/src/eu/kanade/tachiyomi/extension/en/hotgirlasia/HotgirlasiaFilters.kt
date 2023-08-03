package eu.kanade.tachiyomi.extension.en.hotgirlasia

import eu.kanade.tachiyomi.extension.en.hotgirlasia.HotgirlasiaFilters.Companion.findInstance
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList

class HotgirlasiaFilters() {

    companion object {

        fun getFilterList() = FilterList(
            PopularFilter(),
            Filter.Header("Popularity has the least priority"),
            Filter.Separator(),
            ByCountryFilter(),
            BySpecialTags(),
            ByTagFilter(),
        )

        fun buildPopularUrl(baseUrl: String): String {
            return "$baseUrl/${popularFilterList.first().second}"
        }

        fun buildSearchUrl(baseUrl: String, page: Int, filter: FilterList): String {
            val popularFilter = filter.findInstance<PopularFilter>()!!
            val tagFilter = filter.findInstance<ByTagFilter>()!!
            val byCountryFilter = filter.findInstance<ByCountryFilter>()!!
            val bySpecialTags = filter.findInstance<BySpecialTags>()!!

            val tag = tagFilter.state
            val popular = popularFilter.state
            val country = byCountryFilter.state
            val specialTag = bySpecialTags.state

            return if (byCountryList[country].second.isNotBlank()) {
                "$baseUrl/${byCountryList[country].second}/page/$page"
            } else if (specialTagsList[specialTag].second.isNotBlank()) {
                "${baseUrl}${specialTagsList[specialTag].second}/page/$page"
            } else if (tag.isNotBlank()) {
                "$baseUrl/tag/${tag.replace(' ', '-')}"
            } else {
                "$baseUrl/${popularFilterList[popular].second}/page/$page"
            }
        }

        private val popularFilterList = arrayOf<Pair<String, String>>(
            Pair("Top Week", "top-week-viewed"),
            Pair("Top Month", "top-month-viewed"),
            Pair("Top Year", "top-year-viewed"),
        )

        private class PopularFilter(
            values: Array<Pair<String, String>> = popularFilterList,
        ) : Filter.Select<String>("Popular", values.map { it.first }.toTypedArray())

        private val byCountryList = arrayOf(
            Pair("", ""),
            Pair("Chinese", "genre/china"),
            Pair("Korean", "genre/korea"),
            Pair("Japanese", "genre/japan"),
            Pair("Taiwanese", "tag/taiwan-beauty-photo"),
            Pair("Thai", "genre/thai"),
        )

        private class ByCountryFilter(
            values: Array<Pair<String, String>> = byCountryList,
        ) : Filter.Select<String>("Nationality", values.map { it.first }.toTypedArray())

        private class ByTagFilter() : Filter.Text("Tag")

        private val specialTagsList = arrayOf(
            Pair("", ""),
            Pair("Korean idols", "/genre/korea/korean-idols"),
            Pair("Korean Models", "/genre/korea/korean-models"),
            Pair("XIUREN", "/tag/xiuren"),
            Pair("FEILIN", "/tag/feilin"),
            Pair("HUAYAN", "/tag/huayan"),
            Pair("LEYUAN", "/tag/leyuan"),
            Pair("XINGYAN", "/tag/xingyan星颜社"),
            Pair("YOUMI", "/tag/youmi"),
            Pair("MFSTAR", "/tag/mfstar"),
            Pair("UGIRLS", "/tag/ugirls"),
            Pair("IMISS", "/tag/imiss"),
            Pair("XIAOYU", "/tag/xiaoyu"),
            Pair("LIGUI", "/tag/ligui"),
            Pair("MYGIRL", "/tag/mygirl"),
            Pair("DKGIRL", "/tag/dkgirl"),
            Pair("CANDY", "/tag/candy"),
            Pair("MiStar", "/tag/mistar"),
            Pair("YouWu", "/tag/youwu尤物馆/"),
            Pair("Beauty Leg", "/genre/taiwan-hongkong-beauty"),
            Pair("Beauty Photo", "/tag/chinese-beauty-photo"),
            Pair("Big Chest", "/tag/big-chest-photo"),
            Pair("Underwear", "/tag/underwear-beauty-photo"),
            Pair("Cosplay", "/tag/cosplay-photo"),
            Pair("Maid Photo", "/tag/maid-photo"),
        )

        private class BySpecialTags(
            values: Array<Pair<String, String>> = specialTagsList,
        ) : Filter.Select<String>("Special Tags", values.map { it.first }.toTypedArray())

        private inline fun <reified T> Iterable<*>.findInstance() = find { it is T } as? T
    }
}
