package codex.codex_iter.www.awol.application;
import android.app.Application;

import java.io.File;
import java.util.Objects;

public class CleanCacheApplication extends Application {

    private static CleanCacheApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CleanCacheApplication getInstance() {
        return instance;
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                if (children != null) {
                    for (String child : children) {
                        deletedAll = deleteFile(new File(file, child)) && deletedAll;
                    }
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(Objects.requireNonNull(cacheDirectory.getParent()));
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    if (!fileName.equals("lib")) {
                        deleteFile(new File(applicationDirectory, fileName));
                    }
                }
            }
        }
    }

}
