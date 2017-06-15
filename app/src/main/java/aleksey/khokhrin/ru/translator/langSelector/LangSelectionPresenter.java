package aleksey.khokhrin.ru.translator.langSelector;

import java.util.List;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.source.Repository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LangSelectionPresenter implements LangSelectionContract.Presenter {

    private final Repository mRepository;

    LangSelectionContract.View mView;

    private LangSelectionDirection direction;

    @Inject
    LangSelectionPresenter(Repository repository, LangSelectionContract.View tasksView) {
        mRepository = repository;
        mView = tasksView;
    }

    @Override
    public void start() {
    }

    public void setDirection(LangSelectionDirection direction) {
        this.direction = direction;
    }

    @Override
    public List<Language> getRecentlyUsedSourceLangs() {
        return direction == LangSelectionDirection.SOURCE ?
                mRepository.getRecentlyUsedSourceLanguages().blockingGet() :
                mRepository.getRecentlyUsedTargetLanguages().blockingGet();
    }

    @Override
    public List<Language> getLanguages() {
        return mRepository.getLanguages().blockingGet();
    }

    @Override
    public void updateLanguageUsage(Language language, LangSelectionDirection direction) {
        mRepository.updateLanguageUsage(language, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }
}