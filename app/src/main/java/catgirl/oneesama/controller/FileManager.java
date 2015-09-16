package catgirl.oneesama.controller;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import catgirl.oneesama.Application;
import catgirl.oneesama.model.chapter.ui.UiPage;

public class FileManager {

    public static String DOWNLOAD_FOLDER = "chapters";

    public static File getChapterFolder(int chapterId) {
        File folder = new File(Application.getContextOfApplication().getExternalFilesDir(null), DOWNLOAD_FOLDER + File.separator + chapterId);
        if(!folder.isDirectory() && !folder.mkdirs()) {
            Log.e("FileManager", "Could not create folder: " + folder.toString());
            return null;
        }
        return folder;
    }

    public static File getPageFile(int chapterId, UiPage page) {
        String[] parts = page.getUrl().split("/");
        return new File(getChapterFolder(chapterId), parts[parts.length - 1]);
    }

    public static boolean fileExists(int chapterId, UiPage page) {
        File folder = new File(Application.getContextOfApplication().getExternalFilesDir(null), DOWNLOAD_FOLDER + File.separator + chapterId);
        String[] parts = page.getUrl().split("/");
        return new File(folder, parts[parts.length - 1]).exists();
    }

    public static InputStream getInputStream(int chapterId, UiPage page) {
        try {
            return new BufferedInputStream(new FileInputStream(getPageFile(chapterId, page)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getCache(String cacheName) {
        return new File(Application.getContextOfApplication().getExternalFilesDir(null), cacheName);
    }
}
