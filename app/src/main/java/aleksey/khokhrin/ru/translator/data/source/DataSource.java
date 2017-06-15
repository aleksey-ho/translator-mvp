package aleksey.khokhrin.ru.translator.data.source;

import java.util.List;
import java.util.Map;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.Translate;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface DataSource {

    Single<Translate> getTranslate(Language langSource, Language langTarget, String text);

    Single<List<Language>> getLanguages();

    Single<Map<String, String>> loadRemoteLanguages(String languageCode);

    Completable loadLanguages();

    Single<Language> getRecentlyUsedSourceLanguage();

    Single<Language> getRecentlyUsedTargetLanguage();

    Single<List<Language>> getRecentlyUsedSourceLanguages();

    Single<List<Language>> getRecentlyUsedTargetLanguages();

    Completable updateLanguages(Map<String, String> stringMap);

    Completable initLanguages();

    Completable updateLanguageUsage(Language language, LangSelectionDirection direction);

    Completable addTranslate(String textSource, String textTranslate, Language languageSource, Language languageTarget);

    Single<List<Translate>> getTranslatesInHistory();

    Single<List<Translate>> getFavouriteTranslates();

    Completable saveAsFavourite(Translate translate);

    Completable removeFromFavourites(Translate translate);

    Completable clearHistory();

    Completable clearFavourites();

    void initFirstSelectedLanguages();

}
