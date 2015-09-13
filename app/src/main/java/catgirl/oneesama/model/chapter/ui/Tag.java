package catgirl.oneesama.model.chapter.ui;

public class Tag {

    private Integer id;
    private String type;
    private String name;
    private String permalink;

    public Tag(Integer id, String type, String name, String permalink) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.permalink = permalink;
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