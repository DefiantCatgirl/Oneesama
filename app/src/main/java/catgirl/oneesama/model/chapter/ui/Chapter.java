package catgirl.oneesama.model.chapter.ui;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private Integer id;
    private String title;
    private String longTitle;
    private String permalink;
    private List<Tag> tags;
    private List<Page> pages;
    private String releasedOn;
    private String addedOn;

    public Chapter(Integer id, String title, String longTitle, String permalink, List<Tag> tags, List<Page> pages, String releasedOn, String addedOn) {
        this.id = id;
        this.title = title;
        this.longTitle = longTitle;
        this.permalink = permalink;
        this.tags = tags;
        this.pages = pages;
        this.releasedOn = releasedOn;
        this.addedOn = addedOn;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
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
     * @return
     * The longTitle
     */
    public String getLongTitle() {
        return longTitle;
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
     * @return
     * The tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     *
     * @return
     * The pages
     */
    public List<Page> getPages() {
        return pages;
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
     * @return
     * The addedOn
     */
    public String getAddedOn() {
        return addedOn;
    }


}