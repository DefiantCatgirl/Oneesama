package catgirl.oneesama.activity.main.fragments.browse.fragments.series.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import catgirl.oneesama.activity.common.data.model.LazyLoadResult;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.model.SeriesItem;
import catgirl.oneesama.data.network.api.DynastyService;
import rx.Observable;

public class SeriesProvider {

    private final DynastyService service;

    public SeriesProvider(DynastyService service) {
        this.service = service;
    }

    public Observable<LazyLoadResult<SeriesItem>> getMoreSeries() {
        return getNewSeries().map(result -> new LazyLoadResult<>(result, true));
    }

    public Observable<List<SeriesItem>> getNewSeries() {
        return service.getAllSeries()
                .map(result -> {
                    List<SeriesItem> seriesList = new ArrayList<>();

                    for (String key : result.keySet()) {
                        seriesList.addAll(Arrays.asList(result.get(key)));
                    }

                    return seriesList;
                });
    }
}
