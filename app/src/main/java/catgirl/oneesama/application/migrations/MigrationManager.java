package catgirl.oneesama.application.migrations;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MigrationManager {
    // TODO: change when new migrations required
    public static final long CURRENT_BUILD_SCHEMA = 0;

    public static final String MIGRATIONS = "migrations";
    public static final String CURRENT_APP_SCHEMA = "current_schema";

    private Context context;

    public void applyMigrations(Context context) {
        this.context = context;

        // TODO: get rid of if not necessary anymore
//        MigrateChapterNames.migrateChapterNames(this);
//        RemoveBrokenIdChaptersAndTags.removeBrokenItemsAndFiles(this);

        long currentAppSchema = context.getSharedPreferences(MIGRATIONS, Context.MODE_PRIVATE).getLong(CURRENT_APP_SCHEMA, CURRENT_BUILD_SCHEMA);
        long canUpdateToSchema = currentAppSchema;
        boolean canUpdateSchema = true;

        // Every migration must check for itself whether it was applied
        // Sometimes migrations need to be applied several times until they succeed
        for (long i = currentAppSchema; i < CURRENT_BUILD_SCHEMA; i++) {
            for (Migration migration : getSchemaMigrations(i)) {
                if (!migration.migrate(context))
                    canUpdateSchema = false;
            }
            if (canUpdateSchema)
                canUpdateToSchema++;
        }

        context.getSharedPreferences(MIGRATIONS, Context.MODE_PRIVATE).edit().putLong(CURRENT_APP_SCHEMA, canUpdateToSchema).commit();
    }

    public List<Migration> getSchemaMigrations(long currentAppSchema) {
        List<Migration> result = new ArrayList<>();

        // No migrations for Schema 0, just remember the version
        if (currentAppSchema == 0) {
            return result;
        }

        return result;
    }

    public interface Migration {
        boolean migrate(Context context);
    }
}
