package aleksey.khokhrin.ru.translator.main.bookmark;

public class HistoryFragmentListenerModel {
    public interface HistoryFragmentListener {
        void updateHistoryTab();
    }

    private static HistoryFragmentListenerModel mInstance;
    private HistoryFragmentListener mListener;

    private HistoryFragmentListenerModel() {}

    public static HistoryFragmentListenerModel getInstance() {
        if(mInstance == null) {
            mInstance = new HistoryFragmentListenerModel();
        }
        return mInstance;
    }

    public void setListener(HistoryFragmentListener listener) {
        mListener = listener;
    }

    public void updateHistoryTab() {
        if(mListener != null) {
            mListener.updateHistoryTab();
        }
    }

}
