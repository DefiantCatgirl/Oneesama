package catgirl.oneesama.application.migrations;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.application.Application;
import catgirl.oneesama.data.controller.FileManager;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.serializable.Tag;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class RemoveBrokenIdChaptersAndTags {
    public static void removeBrokenItemsAndFiles(Context context) {
        // Due to Dynasty API not having IDs anymore there are broken chapters and tags
        // (usually one of each) in local databases of anyone who tried to download a chapter

        // These are broken beyond repair and need to be removed.

        int success = context.getSharedPreferences("migrations", Context.MODE_PRIVATE).getInt("removeBrokenIdChapters", 0);

        if (success < 2) {
            Observable
                    .fromCallable(() -> {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        RealmResults<Chapter> chapters = realm
                                .allObjects(Chapter.class)
                                .where()
                                .equalTo("tags.id", 0)
                                .findAll();

                        List<Chapter> deleteChapters = new ArrayList<>();
                        deleteChapters.addAll(chapters);
                        for (Chapter chapter : deleteChapters) {
                            Log.d("RemoveBrokenItems", "Removed chapter " + chapter.getTitle());
                            chapter.removeFromRealm();
                        }

                        RealmResults<Tag> tags = realm
                                .allObjects(Tag.class)
                                .where()
                                .equalTo("id", 0)
                                .findAll();

                        List<Tag> deleteTags = new ArrayList<>();
                        deleteTags.addAll(tags);
                        for (Tag tag : deleteTags) {
                            Log.d("RemoveBrokenItems", "Removed tag " + tag.getName());
                            tag.removeFromRealm();
                        }

                        // Remove files belonging to broken chapters

                        RealmResults<Chapter> allChapters = realm
                                .allObjects(Chapter.class);
                        List<Integer> ids = new ArrayList<>();
                        for (Chapter chapter : allChapters) {
                            ids.add(chapter.getId());
                        }

                        File allFolders = new File(Application.getContextOfApplication().getExternalFilesDir(null), FileManager.DOWNLOAD_FOLDER);
                        for (File folder : allFolders.listFiles()) {
                            try {
                                if (folder.isDirectory() && !ids.contains(Integer.parseInt(folder.getName()))) {
                                    FileManager.deleteFolder(Integer.parseInt(folder.getName()));
                                    Log.d("FileManager", "Deleted directory: " + folder.getName());
                                }
                            } catch (NumberFormatException e) {
                                // Skip any odd folder that might occur
                                e.printStackTrace();
                            }
                        }

                        realm.commitTransaction();

                        return true;
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> context.getSharedPreferences("migrations", Context.MODE_PRIVATE).edit().putInt("removeBrokenIdChapters", success + 1).commit(),
                            Throwable::printStackTrace);
        }
    }
}
