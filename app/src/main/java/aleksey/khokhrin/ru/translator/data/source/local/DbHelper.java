package aleksey.khokhrin.ru.translator.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Translator.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TRANSLATE =
            "CREATE TABLE " + PersistenceContract.TranslateEntry.TABLE_NAME + " (" +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE + TEXT_TYPE + COMMA_SEP +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET + TEXT_TYPE + COMMA_SEP +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_DATE + DATE_TYPE + COMMA_SEP +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE + TEXT_TYPE + COMMA_SEP +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET + TEXT_TYPE + COMMA_SEP +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY + INTEGER_TYPE + COMMA_SEP +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_LANGUAGES =
            "CREATE TABLE " + PersistenceContract.LanguageEntry.TABLE_NAME + " (" +
                    PersistenceContract.LanguageEntry.COLUMN_NAME_CODE + TEXT_TYPE + " PRIMARY KEY," +
                    PersistenceContract.LanguageEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE + DATE_TYPE + COMMA_SEP +
                    PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE + DATE_TYPE + COMMA_SEP +
                    PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER + INTEGER_TYPE +
                    " )";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LANGUAGES);
        db.execSQL(SQL_CREATE_TRANSLATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}

