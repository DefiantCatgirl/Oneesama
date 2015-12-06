package catgirl.oneesama.scraper.recentlist;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import catgirl.oneesama.api.Config;
import catgirl.oneesama.scraper.DynastyPage;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DynastyRecentChapterPageProvider {

    // TODO: dagger
    private static DynastyRecentChapterPageProvider _provider;

    public static DynastyRecentChapterPageProvider getProvider() {
        if(_provider == null)
            _provider = new DynastyRecentChapterPageProvider();
        return _provider;
    }

    private DynastyRecentChapterPageProvider() {

    }

    public DynastyRecentChapterPage page;

    public DynastyRecentChapterPage provideSeriesPage() throws Throwable {

        DynastyRecentChapterPage seriesPage = new DynastyRecentChapterPage();
        final Throwable[] error = {null};

        List<Integer> numbers = Arrays.asList(1, 2, 3);
        Observable.from(numbers)
                .concatMapEager(integer -> Observable.fromCallable(() -> parseSinglePage(integer)).subscribeOn(Schedulers.io()))
                .toBlocking()
                .subscribe(seriesPage.chapters::addAll, e -> {
                    error[0] = e;
                });

        if(error[0] != null)
            throw error[0];

        page = seriesPage;

        return seriesPage;
    }

    XPathExpression xpath;
    XPathExpression authorXpath;
    XPathExpression titleXpath;
    XPathExpression doujinXpath;
    XPathExpression tagsXpath;

    private List<DynastyRecentChapterPage.RecentChapter> parseSinglePage(int pageNumber) throws Exception {
//        long ms = System.currentTimeMillis();
        String url = Config.apiEndpoint + "chapters/added?page=" + pageNumber;
        Document doc = DynastyPage.getBody(url);

        if(xpath == null) {
            tagsXpath = XPathFactory.newInstance()
                    .newXPath().compile(".//*[@class='label']");
            doujinXpath = XPathFactory.newInstance()
                    .newXPath().compile(".//a[contains(@href,'/doujins/')]");
            titleXpath = XPathFactory.newInstance()
                    .newXPath().compile(".//*[@class='name']");
            authorXpath = XPathFactory.newInstance()
                    .newXPath().compile(".//a[contains(@href,'/authors/')]");
            xpath = XPathFactory.newInstance()
                    .newXPath().compile("//dd");
        }

        NodeList result = (NodeList) xpath.evaluate(doc, XPathConstants.NODESET);

        List<DynastyRecentChapterPage.RecentChapter> chapters = new ArrayList<>();

        for(int i = 0; i < result.getLength(); i++) {
            Node n = result.item(i);

            DynastyRecentChapterPage.RecentChapter c = new DynastyRecentChapterPage.RecentChapter();

            Node author = (Node) authorXpath.evaluate(n, XPathConstants.NODE);
            if(author != null) {
                c.authorName = author.getTextContent().trim();
                if(c.authorName.startsWith("unknown"))
                    c.authorName = null;
            }

            Node title = (Node) titleXpath.evaluate(n, XPathConstants.NODE);
            if(title != null) {
                c.chapterName = title.getTextContent().trim();
                String[] parts = title.getAttributes().getNamedItem("href").getNodeValue().split("/");
                c.chapterId = parts[parts.length - 1].trim();
            }

            Node doujin = (Node) doujinXpath.evaluate(n, XPathConstants.NODE);
            if(doujin != null)
                c.doujinName = doujin.getTextContent().trim();

            NodeList tags = (NodeList) tagsXpath.evaluate(n, XPathConstants.NODESET);

            for(int j = 0; j < tags.getLength(); j++) {
                c.tags.add(tags.item(j).getTextContent().trim());
            }

            chapters.add(c);
        }

//        Log.v("Debug", "Parsing " + pageNumber + " took " + (System.currentTimeMillis() - ms) + "ms");

        return chapters;
    }
}
