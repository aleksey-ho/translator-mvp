package aleksey.khokhrin.ru.translator.data.source.remote;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.Translate;
import aleksey.khokhrin.ru.translator.data.source.DataSource;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Implementation of the data source.
 */
@Singleton
public class RemoteDataSource implements DataSource {

    static final String ENDPOINT = "https://translate.yandex.net";
    static final String YANDEX_TRANSLATE_API_KEY = "trnsl.1.1.20170409T134624Z.ed295de402b7d3bc.704846fcb4ddb6da80ae8dad8a0f09a3da026688";

    YandexTranslateApi yandexTranslateApi;

    @Inject
    public RemoteDataSource(YandexTranslateApi yandexTranslateApi) {
        this.yandexTranslateApi = yandexTranslateApi;
    }

    @Override
    public Single<Translate> getTranslate(Language langSource, Language langTarget, String text) {
        return Single.create(emitter -> {
            yandexTranslateApi.translate(text.trim(),
                    langSource.getCode() + "-" + langTarget.getCode(),
                    YANDEX_TRANSLATE_API_KEY)
                    .subscribe(translateModel -> {
                        Translate translate = new Translate(text, translateModel.getText().get(0),
                                langSource, langTarget, Calendar.getInstance().getTime(),
                                false, false);
                        emitter.onSuccess(translate);
                    }, error -> {
                        emitter.onError(error);
                    });
        });
    }

    @Override
    public Single<List<Language>> getLanguages() {
        return null;
    }

    @Override
    public Single<Map<String, String>> loadRemoteLanguages(String languageCode) {
        return Single.create(emitter -> {
            yandexTranslateApi.getLangs(languageCode, YANDEX_TRANSLATE_API_KEY)
                    .subscribe(languageModel -> {
                        Map<String, String> langs = languageModel.getLangs();
                        emitter.onSuccess(langs);
                    }, throwable -> {
                        emitter.onError(throwable);
                    });
        });
    }

    @Override
    public Completable loadLanguages() {
        return null;
    }

    @Override
    public Single<Language> getRecentlyUsedSourceLanguage() {
        return null;
    }

    @Override
    public Single<Language> getRecentlyUsedTargetLanguage() {
        return null;
    }

    @Override
    public Single<List<Language>> getRecentlyUsedSourceLanguages() {
        return null;
    }

    @Override
    public Single<List<Language>> getRecentlyUsedTargetLanguages() {
        return null;
    }

    @Override
    public Completable updateLanguages(Map<String, String> stringMap) {
        return null;
    }

    @Override
    public Completable initLanguages() {
        return null;
    }

    @Override
    public Completable updateLanguageUsage(Language language, LangSelectionDirection direction) {
        return null;
    }

    @Override
    public Completable addTranslate(String textSource, String textTranslate, Language languageSource, Language languageTarget) {
        return null;
    }

    @Override
    public Single<List<Translate>> getTranslatesInHistory() {
        return null;
    }

    @Override
    public Single<List<Translate>> getFavouriteTranslates() {
        return null;
    }

    @Override
    public Completable saveAsFavourite(Translate translate) {
        return null;
    }

    @Override
    public Completable removeFromFavourites(Translate translate) {
        return null;
    }

    @Override
    public Completable clearHistory() {
        return null;
    }

    @Override
    public Completable clearFavourites() {
        return null;
    }

    @Override
    public void initFirstSelectedLanguages() {

    }

}
