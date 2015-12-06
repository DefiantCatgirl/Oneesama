package catgirl.oneesama.scraper;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import catgirl.oneesama.api.Config;

public class DynastySeriesPageProvider {
    static Map<String, DynastySeriesPage> cache = new HashMap<>();

    public static DynastySeriesPage provideSeriesPage(String seriesId) throws Exception {

        DynastySeriesPage seriesPage = new DynastySeriesPage();

        String url = Config.apiEndpoint + "series/" + seriesId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        String html = response.body().string();

        Pattern mPattern = Pattern.compile("(?ms)<body>.*</body>");

        Matcher matcher = mPattern.matcher(html);
        matcher.find();
        html = matcher.group();

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(html)));

        XPathExpression xpath = XPathFactory.newInstance()
                .newXPath().compile("//dl");
        XPathExpression chapterXpath = XPathFactory.newInstance()
                .newXPath().compile(".//a");

        NodeList result = (NodeList) xpath.evaluate(doc, XPathConstants.NODESET);

        String current = null;
        for(int i = 0; i < result.item(0).getChildNodes().getLength(); i++) {
            Node n = result.item(0).getChildNodes().item(i);
            if(n.getNodeName().equals("dt")) {
                current = n.getTextContent().trim();
                if(current.isEmpty())
                    current = null;
            }
            else if(n.getNodeName().equals("dd")) {
                Node link = (Node) chapterXpath.evaluate(n, XPathConstants.NODE);

                DynastySeriesPage.Chapter c = new DynastySeriesPage.Chapter();

                String[] parts = link.getAttributes().getNamedItem("href").getNodeValue().split("/");
                c.chapterId = parts[parts.length - 1];
                c.chapterName = link.getTextContent().trim();
                c.volumeName = current;

                seriesPage.chapters.add(c);
            }
        }

        return seriesPage;
    }

    public static DynastySeriesPage.Chapter provideChapterInfo(String seriesId, String chapterId) throws Exception {
        if(cache.containsKey(seriesId)) {
            Log.v("Debug", "Cached");
            DynastySeriesPage.Chapter c = cache.get(seriesId).getChapter(chapterId);
            if (c != null)
                return c;
        }

        DynastySeriesPage seriesPage = provideSeriesPage(seriesId);

        DynastySeriesPage.Chapter result = null;
        for(DynastySeriesPage.Chapter c : seriesPage.chapters) {
            if(c.chapterId.equals(chapterId)) {
                result = c;
                break;
            }
        }

        Log.v("Debug", "Put into cache");
        cache.put(seriesId, seriesPage);

        return result;
    }
}
