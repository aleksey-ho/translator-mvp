package aleksey.khokhrin.ru.translator.langSelector;

import java.util.List;

import aleksey.khokhrin.ru.translator.BasePresenter;
import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;

public interface LangSelectionContract {

    interface View {
    }

    interface Presenter extends BasePresenter {

        List<Language> getRecentlyUsedSourceLangs();

        List<Language> getLanguages();

        void updateLanguageUsage(Language language, LangSelectionDirection direction);
    }
}
