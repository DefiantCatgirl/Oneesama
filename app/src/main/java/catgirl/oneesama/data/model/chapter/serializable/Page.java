package catgirl.oneesama.data.model.chapter.serializable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Page extends RealmObject {

    @Expose
    @Required
    private String name;
    @Expose
    @Required
    private String url;

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
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
