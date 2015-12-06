package catgirl.oneesama.model.chapter.ui;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.model.chapter.serializable.Chapter;
import rx.Observable;

public class UiChapter {
    private Integer id;
    private String title;
    private String longTitle;
    private String permalink;
    private List<UiTag> tags;
    private List<UiPage> pages;
    private String releasedOn;
    private String addedOn;
    private String volumeName;

    private boolean completelyDownloaded;

    public UiChapter(Integer id, String title, String longTitle, String permalink, List<UiTag> tags, List<UiPage> pages, String releasedOn, String addedOn, String volumeName, boolean completelyDownloaded) {
        this.id = id;
        this.title = title;
        this.longTitle = longTitle;
        this.permalink = permalink;
        this.tags = tags;
        this.pages = pages;
        this.releasedOn = releasedOn;
        this.addedOn = addedOn;
        this.volumeName = volumeName;
        this.completelyDownloaded = completelyDownloaded;
    }

    public UiChapter(Chapter chapter) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.longTitle = chapter.getLongTitle();
        this.permalink = chapter.getPermalink();

        Observable.from(chapter.getTags())
                .map(UiTag::new)
                .reduce(new ArrayList<UiTag>(), (tags1, tag) -> {
                    tags1.add(tag);
                    return tags1;
                })
                .subscribe(tags -> this.tags = tags);

        Observable.from(chapter.getPages())
                .map(UiPage::new)
                .reduce(new ArrayList<UiPage>(), (pages1, page) -> {
                    pages1.add(page);
                    return pages1;
                })
                .subscribe(pages -> this.pages = pages);

        this.releasedOn = chapter.getReleasedOn();
        this.addedOn = chapter.getAddedOn();
        this.volumeName = chapter.getVolumeName();
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
    public List<UiTag> getTags() {
        return tags;
    }

    /**
     *
     * @return
     * The pages
     */
    public List<UiPage> getPages() {
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

    public String getVolumeName() {
        return volumeName;
    }


    public boolean isCompletelyDownloaded() {
        return completelyDownloaded;
    }

    public void setCompletelyDownloaded(boolean completelyDownloaded) {
        this.completelyDownloaded = completelyDownloaded;
    }
}
