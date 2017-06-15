package aleksey.khokhrin.ru.translator.data.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.Translate;
import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class Repository implements DataSource {

    private final DataSource mRemoteDataSource;

    private final DataSource mLocalDataSource;

    private List<Translate> cachedTranslates = new ArrayList<>();

    @Inject
    Repository(@Remote DataSource remoteDataSource, @Local DataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    @Override
    public Single<Translate> getTranslate(Language langSource, Language langTarget, String text) {
        return Single.create(emitter -> {
            Translate toTranslate = new Translate(text, langSource, langTarget);
            if (cachedTranslates.contains(toTranslate)) {
                emitter.onSuccess(cachedTranslates.get(cachedTranslates.indexOf(toTranslate)));
            }
            else {
                Translate translate = mRemoteDataSource.getTranslate(langSource, langTarget, text).blockingGet();
                cachedTranslates.add(translate);
                emitter.onSuccess(translate);
            }
        });
    }

    @Override
    public Single<List<Language>> getLanguages() {
        return mLocalDataSource.getLanguages();
    }

    @Override
    public Single<Map<String, String>> loadRemoteLanguages(String languageCode) {
        return Single.create(singleEmitter -> {
            Map<String, String> languages = mRemoteDataSource.loadRemoteLanguages(languageCode).blockingGet();
            if (languages == null || languages.size() == 0)
                languages = mRemoteDataSource.loadRemoteLanguages("en").blockingGet();
            mLocalDataSource.updateLanguages(languages);
            singleEmitter.onSuccess(languages);
        });
    }

    @Override
    public Completable loadLanguages() {
        return Completable.create(completableEmitter -> {
            Map<String, String> languages =
                    mRemoteDataSource.loadRemoteLanguages(Locale.getDefault().getLanguage()).blockingGet();
            if (languages == null || languages.size() == 0)
                languages = mRemoteDataSource.loadRemoteLanguages("en").blockingGet();
            mLocalDataSource.updateLanguages(languages).blockingGet();
            completableEmitter.onComplete();
        });
    }

    @Override
    public Single<Language> getRecentlyUsedSourceLanguage() {
        return mLocalDataSource.getRecentlyUsedSourceLanguage();
    }

    @Override
    public Single<Language> getRecentlyUsedTargetLanguage() {
        return mLocalDataSource.getRecentlyUsedTargetLanguage();
    }

    @Override
    public Single<List<Language>> getRecentlyUsedSourceLanguages() {
        return mLocalDataSource.getRecentlyUsedSourceLanguages();
    }

    @Override
    public Single<List<Language>> getRecentlyUsedTargetLanguages() {
        return mLocalDataSource.getRecentlyUsedTargetLanguages();
    }

    @Override
    public Completable updateLanguages(Map<String, String> stringMap) {
        return mLocalDataSource.updateLanguages(stringMap);
    }

    @Override
    public Completable initLanguages() {
        return Completable.create(observableEmitter -> {

            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("af", "Afrikaans");
            stringMap.put("am", "Amharic");
            stringMap.put("ar", "Arabic");
            stringMap.put("az", "Azerbaijani");
            stringMap.put("ba", "Bashkir");
            stringMap.put("be", "Belarusian");
            stringMap.put("bg", "Bulgarian");
            stringMap.put("bn", "Bengali");
            stringMap.put("bs", "Bosnian");
            stringMap.put("ca", "Catalan");
            stringMap.put("ceb", "Cebuano");
            stringMap.put("cs", "Czech");
            stringMap.put("cy", "Welsh");
            stringMap.put("da", "Danish");
            stringMap.put("de", "German");
            stringMap.put("el", "Greek");
            stringMap.put("en", "English");
            stringMap.put("eo", "Esperanto");
            stringMap.put("es", "Spanish");
            stringMap.put("et", "Estonian");
            stringMap.put("eu", "Basque");
            stringMap.put("fa", "Persian");
            stringMap.put("fi", "Finnish");
            stringMap.put("fr", "French");
            stringMap.put("ga", "Irish");
            stringMap.put("gd", "Scottish Gaelic");
            stringMap.put("gl", "Galician");
            stringMap.put("gu", "Gujarati");
            stringMap.put("he", "Hebrew");
            stringMap.put("hi", "Hindi");
            stringMap.put("hr", "Croatian");
            stringMap.put("ht", "Haitian");
            stringMap.put("hu", "Hungarian");
            stringMap.put("hy", "Armenian");
            stringMap.put("id", "Indonesian");
            stringMap.put("is", "Icelandic");
            stringMap.put("it", "Italian");
            stringMap.put("ja", "Japanese");
            stringMap.put("jv", "Javanese");
            stringMap.put("ka", "Georgian");
            stringMap.put("kk", "Kazakh");
            stringMap.put("km", "Khmer");
            stringMap.put("kn", "Kannada");
            stringMap.put("ko", "Korean");
            stringMap.put("ky", "Kyrgyz");
            stringMap.put("la", "Latin");
            stringMap.put("lb", "Luxembourgish");
            stringMap.put("lo", "Lao");
            stringMap.put("lt", "Lithuanian");
            stringMap.put("lv", "Latvian");
            stringMap.put("mg", "Malagasy");
            stringMap.put("mhr", "Mari");
            stringMap.put("mi", "Maori");
            stringMap.put("mk", "Macedonian");
            stringMap.put("ml", "Malayalam");
            stringMap.put("mn", "Mongolian");
            stringMap.put("mr", "Marathi");
            stringMap.put("mrj", "Hill Mari");
            stringMap.put("ms", "Malay");
            stringMap.put("mt", "Maltese");
            stringMap.put("my", "Burmese");
            stringMap.put("ne", "Nepali");
            stringMap.put("nl", "Dutch");
            stringMap.put("no", "Norwegian");
            stringMap.put("pa", "Punjabi");
            stringMap.put("pap", "Papiamento");
            stringMap.put("pl", "Polish");
            stringMap.put("pt", "Portuguese");
            stringMap.put("ro", "Romanian");
            stringMap.put("ru", "Russian");
            stringMap.put("si", "Sinhalese");
            stringMap.put("sk", "Slovak");
            stringMap.put("sl", "Slovenian");
            stringMap.put("sq", "Albanian");
            stringMap.put("sr", "Serbian");
            stringMap.put("su", "Sundanese");
            stringMap.put("sv", "Swedish");
            stringMap.put("sw", "Swahili");
            stringMap.put("ta", "Tamil");
            stringMap.put("te", "Telugu");
            stringMap.put("tg", "Tajik");
            stringMap.put("th", "Thai");
            stringMap.put("tl", "Tagalog");
            stringMap.put("tr", "Turkish");
            stringMap.put("tt", "Tatar");
            stringMap.put("udm", "Udmurt");
            stringMap.put("uk", "Ukrainian");
            stringMap.put("ur", "Urdu");
            stringMap.put("uz", "Uzbek");
            stringMap.put("vi", "Vietnamese");
            stringMap.put("xh", "Xhosa");
            stringMap.put("yi", "Yiddish");
            stringMap.put("zh", "Chinese");
            updateLanguages(stringMap).blockingGet();
            mLocalDataSource.initFirstSelectedLanguages();
            observableEmitter.onComplete();
        });
    }

    @Override
    public Completable updateLanguageUsage(Language language, LangSelectionDirection direction) {
        return mLocalDataSource.updateLanguageUsage(language, direction);
    }

    @Override
    public Completable addTranslate(String textSource, String textTranslate, Language languageSource, Language languageTarget) {
        return mLocalDataSource.addTranslate(textSource, textTranslate, languageSource, languageTarget);
    }

    @Override
    public Single<List<Translate>> getTranslatesInHistory() {
        return mLocalDataSource.getTranslatesInHistory();
    }

    @Override
    public Single<List<Translate>> getFavouriteTranslates() {
        return mLocalDataSource.getFavouriteTranslates();
    }

    @Override
    public Completable saveAsFavourite(Translate translate) {
        return mLocalDataSource.saveAsFavourite(translate);
    }

    @Override
    public Completable removeFromFavourites(Translate translate) {
        return mLocalDataSource.removeFromFavourites(translate);
    }

    @Override
    public Completable clearHistory() {
        return mLocalDataSource.clearHistory();
    }

    @Override
    public Completable clearFavourites() {
        return mLocalDataSource.clearFavourites();
    }

    @Override
    public void initFirstSelectedLanguages() {
    }

}
