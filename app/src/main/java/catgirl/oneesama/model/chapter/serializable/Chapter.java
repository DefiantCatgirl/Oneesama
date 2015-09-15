package catgirl.oneesama.model.chapter.serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Chapter extends RealmObject {
    @PrimaryKey
    @Expose
    private int id;

    @Expose
    private String title;
    @SerializedName("long_title")
    @Expose
    private String longTitle;
    @Expose
    private String permalink;
    @Expose
    private RealmList<Tag> tags = new RealmList<Tag>();
    @Expose
    private RealmList<Page> pages = new RealmList<Page>();
    @SerializedName("released_on")
    @Expose
    private String releasedOn;
    @SerializedName("added_on")
    @Expose
    private String addedOn;

    private boolean completelyDownloaded = false;

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The longTitle
     */
    public String getLongTitle() {
        return longTitle;
    }

    /**
     *
     * @param longTitle
     * The long_title
     */
    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    /**
     *
     * @return
     * The permalink
     */
    public String getPermalink() {
        return permalink;
    }

    /**
     *
     * @param permalink
     * The permalink
     */
    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    /**
     *
     * @return
     * The tags
     */
    public RealmList<Tag> getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     * The tags
     */
    public void setTags(RealmList<Tag> tags) {
        this.tags = tags;
    }

    /**
     *
     * @return
     * The pages
     */
    public RealmList<Page> getPages() {
        return pages;
    }

    /**
     *
     * @param pages
     * The pages
     */
    public void setPages(RealmList<Page> pages) {
        this.pages = pages;
    }

    /**
     *
     * @return
     * The releasedOn
     */
    public String getReleasedOn() {
        return releasedOn;
    }

    /**
     *
     * @param releasedOn
     * The released_on
     */
    public void setReleasedOn(String releasedOn) {
        this.releasedOn = releasedOn;
    }

    /**
     *
     * @return
     * The addedOn
     */
    public String getAddedOn() {
        return addedOn;
    }

    /**
     *
     * @param addedOn
     * The added_on
     */
    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public boolean isCompletelyDownloaded() {
        return completelyDownloaded;
    }

    public void setCompletelyDownloaded(boolean completelyDownloaded) {
        this.completelyDownloaded = completelyDownloaded;
    }
}