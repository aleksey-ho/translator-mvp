package aleksey.khokhrin.ru.translator.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.Translate;
import aleksey.khokhrin.ru.translator.data.source.DataSource;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    private DatabaseManager databaseManager;

    @Inject
    public LocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        databaseManager = new DatabaseManager(new DbHelper(context));
    }

    static final Translate NOT_FOUND = new Translate();

    @Override
    public Single<Translate> getTranslate(Language langSource, Language langTarget, String text) {
        return Single.create(emitter -> {
            Translate translate = null;
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            String[] projection = {
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_DATE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES
            };
            String selection =
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE + " LIKE ? AND " +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET + " LIKE ? AND " +
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE + " LIKE ? ";

            String[] selectionArgs = { langSource.getCode() , langTarget.getCode(), text };

            Cursor c = db.query(PersistenceContract.TranslateEntry.TABLE_NAME, projection,
                    selection, selectionArgs, null, null, null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                String textTarget = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET));
                Date date = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_DATE)));
                boolean savedInHistory = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY)) == 1;
                boolean savedInFavourites = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES)) == 1;

                translate = new Translate(text, textTarget,
                        langSource, langTarget, date, savedInHistory, savedInFavourites);
            }
            if (c != null) {
                c.close();
            }
            databaseManager.closeDatabase();

            if (translate != null)
                emitter.onSuccess(translate);
            else
                emitter.onSuccess(NOT_FOUND);
        });
    }

    @Override
    public Single<List<Language>> getLanguages() {
        return Single.create(emitter -> {
            List<Language> languages = new ArrayList<>();

            SQLiteDatabase db = databaseManager.getWritableDatabase();

            String[] projection = {
                    PersistenceContract.LanguageEntry.COLUMN_NAME_CODE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_NAME,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER
            };

            String order = PersistenceContract.LanguageEntry.COLUMN_NAME_NAME + " ASC";
            Cursor c = db.query(PersistenceContract.LanguageEntry.TABLE_NAME,
                    projection, null, null, null, null, order);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String code = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_CODE));
                    String name = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_NAME));
                    Date sourceLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE)));
                    Date targetLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE)));
                    int usageCounter = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER));
                    Language language = new Language(code, name, sourceLastUseDate, targetLastUseDate, usageCounter);
                    languages.add(language);
                }
            }
            if (c != null) {
                c.close();
            }
            databaseManager.closeDatabase();
            emitter.onSuccess(languages);
        });
    }

    public Language getLanguageByCode(String code) {
        Language language = null;
        SQLiteDatabase db = databaseManager.getWritableDatabase();

        String[] projection = {
                PersistenceContract.LanguageEntry.COLUMN_NAME_CODE,
                PersistenceContract.LanguageEntry.COLUMN_NAME_NAME,
                PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE,
                PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE,
                PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER
        };
        String selection = PersistenceContract.LanguageEntry.COLUMN_NAME_CODE + " LIKE ?";
        String[] selectionArgs = { code };
        Cursor c = db.query(
                PersistenceContract.LanguageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String name = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_NAME));
            Date sourceLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE)));
            Date targetLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE)));
            int usageCounter = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER));
            language = new Language(code, name, sourceLastUseDate, targetLastUseDate, usageCounter);
        }
        if (c != null) {
            c.close();
        }
        databaseManager.closeDatabase();
        return language;
    }

    @Override
    public Single<Map<String, String>> loadRemoteLanguages(String languageCode) {
        return null;
    }

    @Override
    public Completable loadLanguages() {
        return null;
    }

    @Override
    public Single<Language> getRecentlyUsedSourceLanguage() {
        return getRecentlyUsedLanguage(true);
    }

    @Override
    public Single<Language> getRecentlyUsedTargetLanguage() {
        return getRecentlyUsedLanguage(false);
    }

    @Override
    public Single<List<Language>> getRecentlyUsedSourceLanguages() {
        return getRecentlyUsedLangs(true);
    }

    @Override
    public Single<List<Language>> getRecentlyUsedTargetLanguages() {
        return getRecentlyUsedLangs(false);
    }

    private Single<List<Language>> getRecentlyUsedLangs(boolean isSourceLang) {
        return Single.create(emitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            List<Language> returnList = new ArrayList<>();
            String[] projection = {
                    PersistenceContract.LanguageEntry.COLUMN_NAME_CODE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_NAME,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER
            };

            String order;
            if (isSourceLang)
                order = PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE + " DESC";
            else
                order = PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE + " DESC";

            Cursor c = db.query(
                    PersistenceContract.LanguageEntry.TABLE_NAME, projection, null, null, null, null, order);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {

                    String code = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_CODE));
                    String name = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_NAME));
                    Date sourceLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE)));
                    Date targetLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE)));
                    int usageCounter = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER));

                    if (((isSourceLang && sourceLastUseDate.getTime() == 0) ||
                            (!isSourceLang && targetLastUseDate.getTime() == 0))
                            || returnList.size() > 2)
                        break;
                    returnList.add(new Language(code, name, sourceLastUseDate, targetLastUseDate, usageCounter));
                }
            }
            if (c != null) {
                c.close();
            }
            databaseManager.closeDatabase();
            emitter.onSuccess(returnList);
        });
    }

    /**
     * @returns recently used language
     * @param isSourceLang defines what language should be returned. If TRUE - source language,
     *                     otherwise - target language
     */
    private Single<Language> getRecentlyUsedLanguage(boolean isSourceLang) {
        return Single.create(emitter -> {
            Language language = null;
            SQLiteDatabase db = databaseManager.getWritableDatabase();

            String[] projection = {
                    PersistenceContract.LanguageEntry.COLUMN_NAME_CODE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_NAME,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE,
                    PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER
            };

            String order;

            if (isSourceLang)
                order = PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE + " DESC";
            else
                order = PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE + " DESC";

            Cursor c = db.query(
                    PersistenceContract.LanguageEntry.TABLE_NAME, projection, null, null, null, null, order);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                String code = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_CODE));
                String name = c.getString(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_NAME));
                Date sourceLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE)));
                Date targetLastUseDate = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE)));
                int usageCounter = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER));
                language = new Language(code, name, sourceLastUseDate, targetLastUseDate, usageCounter);
            }
            if (c != null) {
                c.close();
            }
            databaseManager.closeDatabase();
            emitter.onSuccess(language);
        });
    }

    @Override
    public Completable updateLanguages(Map<String, String> stringMap) {
        return Completable.create(observableEmitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();

            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                String[] projection = { PersistenceContract.LanguageEntry.COLUMN_NAME_CODE };
                String selection = PersistenceContract.LanguageEntry.COLUMN_NAME_CODE + " LIKE ?";
                String[] selectionArgs = { entry.getKey() };
                Cursor c = db.query(
                        PersistenceContract.LanguageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

                if (c != null && c.getCount() > 0) {
                    ContentValues values = new ContentValues();
                    values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_NAME, entry.getValue());
                    db.update(PersistenceContract.LanguageEntry.TABLE_NAME, values, selection, selectionArgs);
                }
                else {
                    ContentValues values = new ContentValues();
                    values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_NAME, entry.getValue());
                    values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_CODE, entry.getKey());
                    db.insert(PersistenceContract.LanguageEntry.TABLE_NAME, null, values);
                }
                if (c != null) {
                    c.close();
                }
            }
            databaseManager.closeDatabase();
            observableEmitter.onComplete();
        });
    }

    @Override
    public Completable initLanguages() {

        return null;
    }

    @Override
    public Completable updateLanguageUsage(Language language, LangSelectionDirection direction) {
        return Completable.create(observableEmitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();

            language.updateUsage(direction);
            String selection = PersistenceContract.LanguageEntry.COLUMN_NAME_CODE + " LIKE ?";
            String[] selectionArgs = {language.getCode()};
            ContentValues values = new ContentValues();
            values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_USAGE_COUNTER, language.getUsageCounter());
            values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE, language.getSourceLastUseDate().getTime());
            values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE, language.getTargetLastUseDate().getTime());
            db.update(PersistenceContract.LanguageEntry.TABLE_NAME, values, selection, selectionArgs);

            databaseManager.closeDatabase();
            observableEmitter.onComplete();
        });
    }

    @Override
    public Completable addTranslate(String textSource, String textTranslate, Language languageSource, Language languageTarget) {
        return Completable.create(completableEmitter -> {
            if (textSource == null || Objects.equals(textSource, "")) {
                completableEmitter.onComplete();
                return;
            }
            Translate translate = getTranslate(languageSource, languageTarget, textSource).blockingGet();
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            if (translate == NOT_FOUND) {
                //create new Translate in DB
                ContentValues values = new ContentValues();
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE, languageSource.getCode());
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET, languageTarget.getCode());
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_DATE, Calendar.getInstance().getTime().getTime());
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE, textSource);
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET, textTranslate);
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY, true);
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES, false);
                db.insert(PersistenceContract.TranslateEntry.TABLE_NAME, null, values);
            }
            else {
                //update Date
                String selection =
                        PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE + " LIKE ? AND " +
                                PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET + " LIKE ? AND " +
                                PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE + " LIKE ? ";
                String[] selectionArgs = { languageSource.getCode() , languageTarget.getCode(), textSource };
                ContentValues values = new ContentValues();
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_DATE, Calendar.getInstance().getTime().getTime());
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY, true);
                values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET, textTranslate);
                db.update(PersistenceContract.TranslateEntry.TABLE_NAME, values, selection, selectionArgs);
            }
            databaseManager.closeDatabase();
            completableEmitter.onComplete();
        });
    }

    @Override
    public Single<List<Translate>> getTranslatesInHistory() {
        return Single.create(emitter -> {
            List<Translate> translatesInHistory = new ArrayList<>();
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            String[] projection = {
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_DATE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES
            };
            String order = PersistenceContract.TranslateEntry.COLUMN_NAME_DATE + " DESC";
            String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY + " LIKE ?";
            String[] selectionArgs = { "1" };
            Cursor c = db.query(PersistenceContract.TranslateEntry.TABLE_NAME, projection,
                    selection, selectionArgs, null, null, order);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String textSource = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE));
                    String textTarget = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET));
                    Date date = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_DATE)));
                    String langSourceCode = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE));
                    String langTargetCode = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET));
                    boolean savedInHistory = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY)) == 1;
                    boolean savedInFavourites = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES)) == 1;

                    Language langSource = getLanguageByCode(langSourceCode);
                    Language langTarget = getLanguageByCode(langTargetCode);

                    translatesInHistory.add(new Translate(textSource, textTarget,
                            langSource, langTarget, date, savedInHistory, savedInFavourites));
                }
            }
            if (c != null) {
                c.close();
            }
            databaseManager.closeDatabase();

            emitter.onSuccess(translatesInHistory);
        });
    }

    @Override
    public Single<List<Translate>> getFavouriteTranslates() {
        return Single.create(emitter -> {
            List<Translate> translatesInHistory = new ArrayList<>();
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            String[] projection = {
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_DATE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY,
                    PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES
            };
            String order = PersistenceContract.TranslateEntry.COLUMN_NAME_DATE + " DESC";
            String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES + " LIKE ?";
            String[] selectionArgs = { "1" };
            Cursor c = db.query(PersistenceContract.TranslateEntry.TABLE_NAME, projection,
                    selection, selectionArgs, null, null, order);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String textSource = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE));
                    String textTarget = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_TARGET));
                    Date date = new Date(c.getLong(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_DATE)));
                    String langSourceCode = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE));
                    String langTargetCode = c.getString(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET));
                    boolean savedInHistory = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY)) == 1;
                    boolean savedInFavourites = c.getInt(c.getColumnIndexOrThrow(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES)) == 1;

                    Language langSource = getLanguageByCode(langSourceCode);
                    Language langTarget = getLanguageByCode(langTargetCode);

                    translatesInHistory.add(new Translate(textSource, textTarget,
                            langSource, langTarget, date, savedInHistory, savedInFavourites));
                }
            }
            if (c != null) {
                c.close();
            }
            databaseManager.closeDatabase();

            emitter.onSuccess(translatesInHistory);
        });
    }

    @Override
    public Completable saveAsFavourite(Translate translate) {
        return Completable.create(observableEmitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();

            String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE +
                    " LIKE ? AND " + PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET +
                    " LIKE ? AND " + PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE +
                    " LIKE ? ";
            String[] selectionArgs = { translate.getLanguageSource().getCode(),
                    translate.getLanguageTarget().getCode(), translate.getTextSource() };
            ContentValues values = new ContentValues();
            values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES, 1);
            db.update(PersistenceContract.TranslateEntry.TABLE_NAME, values, selection, selectionArgs);

            translate.setSavedInFavourites(true);

            databaseManager.closeDatabase();
            observableEmitter.onComplete();
        });
    }

    @Override
    public Completable removeFromFavourites(Translate translate) {
        return Completable.create(observableEmitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();

            String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_SOURCE +
                    " LIKE ? AND " + PersistenceContract.TranslateEntry.COLUMN_NAME_LANG_TARGET +
                    " LIKE ? AND " + PersistenceContract.TranslateEntry.COLUMN_NAME_TEXT_SOURCE +
                    " LIKE ? ";
            String[] selectionArgs = { translate.getLanguageSource().getCode(),
                    translate.getLanguageTarget().getCode(), translate.getTextSource() };
            ContentValues values = new ContentValues();
            values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES, 0);
            db.update(PersistenceContract.TranslateEntry.TABLE_NAME, values, selection, selectionArgs);

            translate.setSavedInFavourites(false);

            databaseManager.closeDatabase();
            observableEmitter.onComplete();
        });
    }

    @Override
    public Completable clearHistory() {
        return Completable.create(emitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY +
                    " LIKE ? ";
            String[] selectionArgs = { "1" };
            ContentValues values = new ContentValues();
            values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY, 0);
            db.update(PersistenceContract.TranslateEntry.TABLE_NAME, values, selection, selectionArgs);
            databaseManager.closeDatabase();
            deleteUnnecessaryBookmarks();
            emitter.onComplete();
        });
    }

    @Override
    public Completable clearFavourites() {
        return Completable.create(emitter -> {
            SQLiteDatabase db = databaseManager.getWritableDatabase();
            String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES +
                    " LIKE ? ";
            String[] selectionArgs = { "1" };
            ContentValues values = new ContentValues();
            values.put(PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES, 0);
            db.update(PersistenceContract.TranslateEntry.TABLE_NAME, values, selection, selectionArgs);
            databaseManager.closeDatabase();
            deleteUnnecessaryBookmarks();
            emitter.onComplete();
        });
    }

    public void deleteUnnecessaryBookmarks() {
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        String selection = PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_HISTORY +
                " LIKE ? AND " + PersistenceContract.TranslateEntry.COLUMN_NAME_SAVED_IN_FAVOURITES +
                " LIKE ? ";
        String[] selectionArgs = { "0", "0" };
        db.delete(PersistenceContract.TranslateEntry.TABLE_NAME, selection, selectionArgs);
        databaseManager.closeDatabase();
    }

    @Override
    public void initFirstSelectedLanguages() {
        SQLiteDatabase db = databaseManager.getWritableDatabase();

        String selection = PersistenceContract.LanguageEntry.COLUMN_NAME_CODE + " LIKE ?";
        String[] selectionArgs = { "en" };
        ContentValues values = new ContentValues();
        values.put(PersistenceContract.LanguageEntry.COLUMN_NAME_SOURCE_LAST_USE_DATE, Calendar.getInstance().getTime().getTime());
        db.update(PersistenceContract.LanguageEntry.TABLE_NAME, values, selection, selectionArgs);

        selectionArgs[0] = "ru";
        ContentValues values2 = new ContentValues();
        values2.put(PersistenceContract.LanguageEntry.COLUMN_NAME_TARGET_LAST_USE_DATE, Calendar.getInstance().getTime().getTime());
        db.update(PersistenceContract.LanguageEntry.TABLE_NAME, values2, selection, selectionArgs);

        databaseManager.closeDatabase();
    }

}
