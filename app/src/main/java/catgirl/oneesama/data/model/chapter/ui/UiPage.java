package catgirl.oneesama.data.model.chapter.ui;

import catgirl.oneesama.data.model.chapter.serializable.Page;

public class UiPage {

    private String name;
    private String url;

    public UiPage(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public UiPage(Page page) {
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
