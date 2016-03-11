package catgirl.oneesama.data.controller;

import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import catgirl.oneesama.application.Application;
import catgirl.oneesama.data.model.chapter.ui.UiPage;

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

    public static void deleteFolder(int chapterId) {
        // Some devices have bugs with re-creating formerly deleted directories, hence this workaround
        try {
            File deleted = new File(Application.getContextOfApplication().getExternalFilesDir(null), "deleted/" + chapterId);
            deleted.mkdirs();
            getChapterFolder(chapterId).renameTo(deleted);
            FileUtils.deleteDirectory(deleted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
