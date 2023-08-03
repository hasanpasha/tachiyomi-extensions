package eu.kanade.tachiyomi.extension.en.hotgirlasia

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
class Hotgirlasia : ParsedHttpSource() {

    override val name = "Hotgirlasia"

    override val baseUrl = "https://hotgirl.asia"

    override val lang = "en"

    override val supportsLatest = true

    // Popular
    override fun popularMangaRequest(page: Int): Request {
        return GET(HotgirlasiaFilters.buildPopularUrl(baseUrl), headers)
    }

    override fun popularMangaSelector() =
        "div.movies-list.movies-list-full div.ml-item"

    override fun popularMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            setUrlWithoutDomain(element.select("a.ml-mask.jt").attr("href"))
            title = element.select("a.ml-mask.jt").attr("oldtitle")
            thumbnail_url = element.select("img.mli-thumb").attr("src")
        }

    override fun popularMangaNextPageSelector() = "div.exxx" // There is no next page

    // Latest
    override fun latestUpdatesRequest(page: Int): Request =
        GET("$baseUrl/photos/page/$page/", headers)

    override fun latestUpdatesSelector() =
        popularMangaSelector()

    override fun latestUpdatesFromElement(element: Element): SManga =
        popularMangaFromElement(element)

    override fun latestUpdatesNextPageSelector() =
        "ul.pagination li.active + li"

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val filterList = if (filters.isEmpty()) getFilterList() else filters

        return if (query.isBlank()) {
            GET(HotgirlasiaFilters.buildSearchUrl(baseUrl, page, filterList), headers)
        } else {
            GET("$baseUrl/page/$page/?s=${query.replace(' ', '+')}", headers)
        }
    }

    override fun searchMangaSelector() =
        popularMangaSelector()

    override fun searchMangaFromElement(element: Element): SManga =
        popularMangaFromElement(element)

    override fun searchMangaNextPageSelector() =
        latestUpdatesNextPageSelector()

    // Details
    override fun mangaDetailsParse(document: Document) =
        SManga.create().apply {
            title = document.select("h3[itemprop=name]").text()
            description = document.select("div.mvic-info").text().trim()
            genre = document.select("div#mv-keywords a[rel=tag]").joinToString { it.text() }
            status = SManga.COMPLETED
            thumbnail_url = document.select("div.thumb.mvic-thumb img").attr("src")
        }

    // Chapters
    override fun chapterListParse(response: Response): List<SChapter> {
        return listOf(
            SChapter.create().apply {
                name = "images"
                url = response.request.url.encodedPath + "/?stype=slideshow"
            },
        )
    }

    override fun chapterFromElement(element: Element) = throw UnsupportedOperationException("Not used")

    override fun chapterListSelector() = throw UnsupportedOperationException("Not used")

    // Pages
    override fun pageListParse(document: Document): List<Page> =
        document.select("div.carousel-inner div.item img").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }

    override fun imageUrlParse(document: Document) = throw UnsupportedOperationException("Not used")

    override fun getFilterList(): FilterList = HotgirlasiaFilters.getFilterList()
}
