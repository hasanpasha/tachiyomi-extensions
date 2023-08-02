package eu.kanade.tachiyomi.extension.en.asiaontop

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
class Asiaontop : ParsedHttpSource() {

    override val name = "Asiaontop"

    override val baseUrl = "https://asiaon.top"

    override val lang = "en"

    override val supportsLatest = false

//    override val client: OkHttpClient = network.cloudflareClient

    // Popular
    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/page/$page/", headers)

    override fun popularMangaSelector() =
        "div#content_masonry div.post_grid_content_wrapper"

    override fun popularMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            setUrlWithoutDomain(element.select("div.image-post-thumb a.link_image.featured-thumbnail").attr("href"))
            title = element.select("div.image-post-thumb a.link_image.featured-thumbnail").attr("title")
            thumbnail_url = element.select("div.image-post-thumb a.link_image.featured-thumbnail img").attr("src")
        }

    override fun popularMangaNextPageSelector() =
        "a.next.page-numbers"

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
        val url = AsiaontopFilters.buildUrl(baseUrl, page, filters)

        if (url == baseUrl) {
            return GET("$baseUrl/?s=${query.replace(' ', '+')}", headers)
        } else {
            println(url)
            return GET(url, headers)
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
            title = document.select("h1.single_post_title_main").text()
//            description = document.select("div p.flex").text().trim()
            genre = document.select("ul.single_post_tag_layout a").joinToString { it.text() }
            status = SManga.ONGOING
            thumbnail_url = document.select("div.image-post-thumb.jlsingle-title-above img").attr("src")
        }

    fun getTime(document: Document): Long {
        val timeString = document.select("span.post-date.updated").attr("datetime")

        return SimpleDateFormat("MMMM d, y").parse(timeString)?.time ?: 0L
    }

    // Chapters
    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        return listOf(
            SChapter.create().apply {
                name = "chapter"
                date_upload = getTime(document)
                setUrlWithoutDomain(response.request.url.encodedPath)
            },
        )
    }

    override fun chapterFromElement(element: Element) = throw UnsupportedOperationException("Not used")

    override fun chapterListSelector() = throw UnsupportedOperationException("Not used")

    // Pages
    override fun pageListParse(document: Document): List<Page> =
        document.select("div.modula-items div.modula-item-content a").mapIndexed { index, element ->
            Page(index, "", element.attr("href"))
        }

    override fun imageUrlParse(document: Document) =
        throw UnsupportedOperationException("Not used")

    override fun getFilterList(): FilterList = AsiaontopFilters.getFilterList()
}
