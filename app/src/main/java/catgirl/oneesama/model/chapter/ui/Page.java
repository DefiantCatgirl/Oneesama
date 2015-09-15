package catgirl.oneesama.model.chapter.ui;

public class Page {

    private String name;
    private String url;

    public Page(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Page(catgirl.oneesama.model.chapter.gson.Page page) {
        this.name = page.getName();
        this.url = page.getUrl();
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

}