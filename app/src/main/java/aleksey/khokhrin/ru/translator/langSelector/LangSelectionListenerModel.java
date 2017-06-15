package aleksey.khokhrin.ru.translator.langSelector;

import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;

public class LangSelectionListenerModel {

    public interface LangSelectionListener {
        void langSelected(Language language, LangSelectionDirection direction);
    }

    private static LangSelectionListenerModel mInstance;
    private LangSelectionListener mListener;

    private LangSelectionListenerModel() {}

    public static LangSelectionListenerModel getInstance() {
        if(mInstance == null) {
            mInstance = new LangSelectionListenerModel();
        }
        return mInstance;
    }

    public void setListener(LangSelectionListener listener) {
        mListener = listener;
    }

    public void langSelected(Language language, LangSelectionDirection direction) {
        if(mListener != null) {
            mListener.langSelected(language, direction);
        }
    }

}
