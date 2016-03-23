package catgirl.oneesama.activity.common.data.model;

import java.util.ArrayList;
import java.util.List;

public class LazyLoadResult<Model> {
    public boolean finished = true;
    public List<Model> elements = new ArrayList<>();

    public LazyLoadResult(List<Model> elements, boolean finished) {
        this.elements = elements;
        this.finished = finished;
    }
}
