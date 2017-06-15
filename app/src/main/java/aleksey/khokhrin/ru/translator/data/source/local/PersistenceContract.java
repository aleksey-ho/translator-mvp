package aleksey.khokhrin.ru.translator.data.source.local;

public class PersistenceContract {

    private PersistenceContract() {}

    public static abstract class TranslateEntry {
        public static final String TABLE_NAME = "translate";
        public static final String COLUMN_NAME_LANG_SOURCE = "languageSource";
        public static final String COLUMN_NAME_LANG_TARGET = "languageTarget";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TEXT_SOURCE = "textSource";
        public static final String COLUMN_NAME_TEXT_TARGET = "textTarget";
        public static final String COLUMN_NAME_SAVED_IN_HISTORY = "savedInHistory";
        public static final String COLUMN_NAME_SAVED_IN_FAVOURITES = "savedInFavourites";
    }

    public static abstract class LanguageEntry {
        public static final String TABLE_NAME = "language";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SOURCE_LAST_USE_DATE = "sourceLastUseDate";
        public static final String COLUMN_NAME_TARGET_LAST_USE_DATE = "targetLastUseDate";
        public static final String COLUMN_NAME_USAGE_COUNTER = "usageCounter";
    }
}
