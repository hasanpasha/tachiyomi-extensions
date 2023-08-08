package eu.kanade.tachiyomi.extension.en.bestprettygirl

import android.annotation.SuppressLint
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat

class Bestprettygirl : ParsedHttpSource() {

    override val name = "Bestprettygirl"

    override val baseUrl = "https://bestprettygirl.com"

    override val lang = "en"

    override val supportsLatest = false

    // Popular
    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/page/$page", headers)

    override fun popularMangaSelector() =
        "div.elementor-posts-container article.elementor-post"

    override fun popularMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            setUrlWithoutDomain(element.select("a").attr("href"))
            title = element.select("div.elementor-post__text").text()
            thumbnail_url = element.select("a img").attr("src")
        }

    override fun popularMangaNextPageSelector() =
        "a.page-numbers.next"

    // Latest
    override fun latestUpdatesRequest(page: Int): Request =
        GET("$baseUrl/last-update", headers)

    override fun latestUpdatesSelector() =
        popularMangaSelector()

    override fun latestUpdatesFromElement(element: Element): SManga =
        popularMangaFromElement(element)

    override fun latestUpdatesNextPageSelector() =
        popularMangaNextPageSelector()

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val filterList = if (filters.isEmpty()) getFilterList() else filters

        val url = BestprettygirlFilters.buildUrl(baseUrl, page, filterList)
        return if (url.isBlank()) {
            GET("$baseUrl/page/$page/?s=${query.replace(' ', '+')}", headers)
        } else {
            GET(url, headers)
        }
    }

    override fun searchMangaSelector() =
        popularMangaSelector()

    override fun searchMangaFromElement(element: Element): SManga =
        popularMangaFromElement(element)

    override fun searchMangaNextPageSelector() =
        popularMangaNextPageSelector()

    // Details
    override fun mangaDetailsParse(document: Document) =
        SManga.create().apply {
            title = document.select("elementor-heading-title.elementor-size-large").text()
            description = document.select("div.elementor-widget-container center + p + p").text().trim()
            status = SManga.COMPLETED
            thumbnail_url = document.select("div.elementor-widget-container p img.aligncenter.size-full[fifu-featured=\"1\"]").attr("src")
        }

    @SuppressLint("SimpleDateFormat")
    fun getTime(document: Document): Long {
        val timeString = document.select("span.elementor-icon-list-text.elementor-post-info__item.elementor-post-info__item--type-date").text()

        val time = SimpleDateFormat("MMMM d, y").parse(timeString)?.time ?: 0L
        println(time)

        return time
    }

    // Chapters
    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        return listOf(
            SChapter.create().apply {
                name = "images"
                date_upload = getTime(document)
                setUrlWithoutDomain(response.request.url.encodedPath)
            },
        )
    }

    override fun chapterFromElement(element: Element) = throw UnsupportedOperationException("Not used")

    override fun chapterListSelector() = throw UnsupportedOperationException("Not used")

    // Pages
    override fun pageListParse(document: Document): List<Page> =
        document.select("div.elementor-widget-container p img.aligncenter.size-full").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }

    override fun imageUrlParse(document: Document) =
        throw UnsupportedOperationException("Not used")

    override fun getFilterList(): FilterList = BestprettygirlFilters.getFilterList()
}
