package eu.kanade.tachiyomi.extension.en.bestprettygirl

import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList

class BestprettygirlFilters() {

    companion object {

        fun getFilterList() = FilterList(
            CategoryFilter(),
            Filter.Separator(),
            ChineseSubCategoryFilter(),
        )

        fun buildUrl(baseUrl: String, page: Int, filter: FilterList): String {
            val categoryFilter = filter.findInstance<CategoryFilter>()!!
            val chineseSubCategoryFilter = filter.findInstance<ChineseSubCategoryFilter>()!!

            val category = categoryFilter.state
            val chineseSub = chineseSubCategoryFilter.state

            if (category != 0) {
                if (category == CHINA_CATEGORY && chineseSub != 0) {
                    return "$baseUrl/${chineseSubCategoryList[chineseSub].second}/page/$page"
                }
                return "$baseUrl/${categoryFilterList[category].second}/page/$page"
            }
            return ""
        }

        private const val CHINA_CATEGORY = 5

        private val categoryFilterList = arrayOf<Pair<String, String>>(
            Pair("", ""),
            Pair("Cosplay", "category/cosplay"),
            Pair("Europe", "category/eu-girls"),
            Pair("Korean", "category/korean"),
            Pair("Japan", "category/japan"),
            Pair("China", "category/china"),
            Pair("AIModel", "category/aimodel"),
        )

        private class CategoryFilter(
            values: Array<Pair<String, String>> = categoryFilterList,
        ) : Filter.Select<String>("Category", values.map { it.first }.toTypedArray())

        private val chineseSubCategoryList = arrayOf(
            Pair("", ""),
            Pair("Beautyleg", "category/china/beautyleg"),
            Pair("Candy网红馆", "category/china/candy"),
            Pair("FeiLin嗲囡囡", "category/china/feilin"),
            Pair("HuaYang花漾Show", "category/china/huayang"),
            Pair("IMiss爱蜜社", "category/china/imiss"),
            Pair("Ligui丽柜", "category/china/ligui"),
            Pair("MFStar模范学院", "category/china/mfstar"),
            Pair("MiStar魅妍社", "category/china/mistar"),
            Pair("MyGirl美媛馆", "category/china/mygirl"),
            Pair("Ugirls尤果圈", "category/china/ugirls"),
            Pair("XiaoYu语画界", "category/china/xiaoyu"),
            Pair("XingYan星颜社", "category/china/xingyan"),
            Pair("XiuRen秀人网", "category/china/xiuren"),
            Pair("YALAYI雅拉伊", "category/china/yalayi"),
            Pair("YouMi尤蜜荟", "category/china/youmi"),
            Pair("YouWu尤物馆", "category/china/youwu"),
        )

        private class ChineseSubCategoryFilter(
            values: Array<Pair<String, String>> = chineseSubCategoryList,
        ) : Filter.Select<String>("Chinese Sub Category", values.map { it.first }.toTypedArray())

        private inline fun <reified T> Iterable<*>.findInstance() = find { it is T } as? T
    }
}
