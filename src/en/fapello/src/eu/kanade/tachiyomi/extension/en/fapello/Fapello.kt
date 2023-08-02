package eu.kanade.tachiyomi.extension.en.fapello

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

class Fapello : ParsedHttpSource() {

    override val name = "Fapello"

    override val baseUrl = "https://fapello.com"

    override val lang = "en"

    override val supportsLatest = false

//    override val client: OkHttpClient = network.cloudflareClient

    // Popular
    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/top-likes/page-$page", headers)

    override fun popularMangaSelector() =
        "div#content div.mt-6 div.bg-yellow-400"

    override fun popularMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            setUrlWithoutDomain(element.select("> a").attr("href"))
            title = element.text()
            thumbnail_url = element.select("div.p-1 img").attr("src")
        }

    override fun popularMangaNextPageSelector() =
        "div#next_page a"

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
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request =
        GET("$baseUrl/search/$query", headers)

    override fun searchMangaSelector() =
        "div#content div.my-3 div.bg-red-400"

    override fun searchMangaFromElement(element: Element): SManga =
        popularMangaFromElement(element)

    override fun searchMangaNextPageSelector() =
        popularMangaNextPageSelector()

    // Details
    override fun mangaDetailsParse(document: Document) =
        SManga.create().apply {
            title = document.select("h2.font-semibold").text()
            description = document.select("div p.flex").text().trim()
//            genre = document.select(".novel-categories > a").joinToString { it.text() }
            status = SManga.ONGOING
            thumbnail_url = document.select(".container .flex img").attr("src")
        }

    // Chapters
    override fun chapterListSelector() =
        "div#content a"

    private fun chapterPageMaxSelector() = "div#showmore"

    private fun chapterNextPageSelector() = popularMangaNextPageSelector()

    override fun chapterListParse(response: Response): List<SChapter> {
        val allElements = mutableListOf<Element>()
        var document = response.asJsoup()
        var con = true

        val maxPages = document.select(chapterPageMaxSelector()).attr("data-max")

        while (con) {
            val currentPageNumber = document.select(chapterPageMaxSelector()).attr("data-page")

            val pageChapters = document.select(chapterListSelector())
            if (pageChapters.isEmpty()) {
                break
            }

            allElements += pageChapters

            val hasNextPage = document.select(chapterNextPageSelector()).isNotEmpty()
            if (!hasNextPage) {
                break
            }

            val nextUrl = document.select(chapterNextPageSelector()).attr("href")
            if (currentPageNumber.equals(maxPages)) {
                con = false
            }

            document = client.newCall(GET(nextUrl, headers)).execute().asJsoup()
        }

        return allElements.map { chapterFromElement(it) }
    }

    private fun videoSelector() = "div.absolute.flex.h-full.items-center.justify-center.w-8.w-full.bg-black.bg-opacity-10"

    override fun chapterFromElement(element: Element): SChapter =
        SChapter.create().apply {
            val isVideo = element.select(videoSelector()).isNotEmpty()
            setUrlWithoutDomain(element.attr("href"))
            name = element.attr("href").trimEnd('/').split('/').last()
            chapter_number = name.toFloat()

            if (isVideo) {
                name = "[video] $name"
            }
        }

    // Pages
    override fun pageListParse(document: Document): List<Page> =
        document.select("div.mx-auto div.flex.justify-between.items-center > a img").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }

    override fun imageUrlParse(document: Document) =
        throw UnsupportedOperationException("Not used")
}
