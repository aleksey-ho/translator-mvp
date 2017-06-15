package aleksey.khokhrin.ru.translator.main.transtlate;

import java.net.UnknownHostException;
import java.util.Objects;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.source.Repository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;

public class TranslatePresenter implements TranslateContract.Presenter {

    private final Repository mRepository;
    TranslateContract.View mView;

    Language languageSource, languageTarget;
    String translate;
    String textSource;

    @Inject
    TranslatePresenter(Repository repository, TranslateContract.View tasksView) {
        mRepository = repository;
        mView = tasksView;
    }

    @Override
    public void start() {
    }

    public void loadLanguages() {
        mRepository.getRecentlyUsedSourceLanguage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(language -> {
                    languageSource = language;
                    mView.setLanguageSource(languageSource.getName());
                }, Throwable::printStackTrace);

        mRepository.getRecentlyUsedTargetLanguage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(language -> {
                    languageTarget = language;
                    mView.setLanguageTarget(languageTarget.getName());
                }, Throwable::printStackTrace);
    }

    @Override
    public void setLanguageSource(Language language) {
        languageSource = language;
        mView.setLanguageSource(languageSource.getName());
    }

    @Override
    public void setLanguageTarget(Language language) {
        languageTarget = language;
        mView.setLanguageTarget(languageTarget.getName());
    }

    /**
     * Translates text and saves result in database
     */
    @Override
    public void translateText(String text) {
        translateText(text, true);
    }

    @Override
    public void swapLanguages() {
        Language tempLanguageSource = this.languageSource;
        languageSource = languageTarget;
        languageTarget = tempLanguageSource;
        mView.setLanguageSource(languageSource.getName());
        mView.setLanguageTarget(languageTarget.getName());
        translateText(textSource);
        mRepository.updateLanguageUsage(languageSource, LangSelectionDirection.SOURCE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, Throwable::printStackTrace);
        mRepository.updateLanguageUsage(languageTarget, LangSelectionDirection.TARGET)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

    @Override
    public void translateText(String text, boolean saveOnCompleted) {
        textSource = text;
        if (textSource == null || Objects.equals(textSource, "")) {
            clearTranslate();
            return;
        }
        mRepository.getTranslate(languageSource, languageTarget, textSource)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translate -> {
                    mView.hideInternetConnectionError();
                    setTranslate(translate.getTextTarget());
                    if (saveOnCompleted)
                        saveTranslate();
                }, error -> {
                    if (error instanceof UnknownHostException) {
                        mView.showInternetConnectionError();
                        clearTranslate();
                    }
                    else {
                        mView.hideInternetConnectionError();
                        if (error instanceof HttpException) {
                            mView.showHttpException((HttpException)error);
                        }
                    }
                });

    }

    @Override
    public void setSourceText(String text) {
        textSource = text;
        mView.setSourceText(text);
    }

    @Override
    public void setTranslate(String translate) {
        this.translate = translate;
        mView.setTranslate(translate);
    }

    @Override
    public void clearSourceText() {
        setSourceText("");
    }

    @Override
    public void clearTranslate() {
        setTranslate("");
    }

    @Override
    public void saveTranslate() {
        mRepository.addTranslate(textSource, translate, languageSource, languageTarget)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

}