package catgirl.oneesama.scraper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

public class DynastyPage {
    public static Document getBody(String url) throws Exception {
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

        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(html)));
    }
}
