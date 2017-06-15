package aleksey.khokhrin.ru.translator.main.transtlate;

import aleksey.khokhrin.ru.translator.BasePresenter;
import aleksey.khokhrin.ru.translator.data.Language;
import retrofit2.adapter.rxjava2.HttpException;

public interface TranslateContract {

    interface View {

        void setLanguageSource(String name);

        void setLanguageTarget(String name);

        void setTranslate(String translate);

        void setSourceText(String text);

        void hideInternetConnectionError();

        void showInternetConnectionError();

        void showHttpException(HttpException exception);
    }

    interface Presenter extends BasePresenter {

        void loadLanguages();

        void setLanguageSource(Language language);

        void setLanguageTarget(Language language);

        void translateText(String text);

        void swapLanguages();

        void translateText(String text, boolean saveOnCompleted);

        void setSourceText(String text);

        void setTranslate(String translate);

        void clearSourceText();

        void clearTranslate();

        void saveTranslate();
    }
}
