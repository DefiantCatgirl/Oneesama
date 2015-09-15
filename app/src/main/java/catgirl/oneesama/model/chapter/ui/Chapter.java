package catgirl.oneesama.model.chapter.ui;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class Chapter {
    private Integer id;
    private String title;
    private String longTitle;
    private String permalink;
    private List<Tag> tags;
    private List<Page> pages;
    private String releasedOn;
    private String addedOn;

    private boolean completelyDownloaded;

    public Chapter(Integer id, String title, String longTitle, String permalink, List<Tag> tags, List<Page> pages, String releasedOn, String addedOn, boolean completelyDownloaded) {
        this.id = id;
        this.title = title;
        this.longTitle = longTitle;
        this.permalink = permalink;
        this.tags = tags;
        this.pages = pages;
        this.releasedOn = releasedOn;
        this.addedOn = addedOn;
        this.completelyDownloaded = completelyDownloaded;
    }

    public Chapter(catgirl.oneesama.model.chapter.gson.Chapter chapter) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.longTitle = chapter.getLongTitle();
        this.permalink = chapter.getPermalink();

        Observable.from(chapter.getTags())
                .map(Tag::new)
                .reduce(new ArrayList<Tag>(), (tags1, tag) -> {
                    tags1.add(tag);
                    return tags1;
                })
                .subscribe(tags -> this.tags = tags);

        Observable.from(chapter.getPages())
                .map(Page::new)
                .reduce(new ArrayList<Page>(), (pages1, page) -> {
                    pages1.add(page);
                    return pages1;
                })
                .subscribe(pages -> this.pages = pages);

        this.releasedOn = chapter.getReleasedOn();
        this.addedOn = chapter.getAddedOn();
        this.completelyDownloaded = chapter.isCompletelyDownloaded();
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


    public boolean isCompletelyDownloaded() {
        return completelyDownloaded;
    }

    public void setCompletelyDownloaded(boolean completelyDownloaded) {
        this.completelyDownloaded = completelyDownloaded;
    }
}