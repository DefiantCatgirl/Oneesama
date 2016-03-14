package catgirl.oneesama.data.model.chapter.ui;

import catgirl.oneesama.data.model.chapter.serializable.Tag;

public class UiTag {

    private Integer id;
    private String type;
    private String name;
    private String permalink;

    public final static String SERIES = "Series";
    public final static String DOUJIN = "Doujin";
    public final static String AUTHOR = "Author";
    public final static String GENERAL = "General";

    public final static String LEFT_TO_RIGHT = "read_left_to_right";

    public UiTag(Integer id, String type, String name, String permalink) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.permalink = permalink;
    }

    public UiTag(Tag tag) {
        this.id = tag.getId();
        this.type = tag.getType();
        this.name = tag.getName();
        this.permalink = tag.getPermalink();
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
     * The type
     */
    public String getType() {
        return type;
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
     * The permalink
     */
    public String getPermalink() {
        return permalink;
    }

}
