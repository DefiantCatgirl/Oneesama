package catgirl.oneesama.model.chapter.ui;

public class Page {

    private String name;
    private String url;

    public Page(String name, String url) {
        this.name = name;
        this.url = url;
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