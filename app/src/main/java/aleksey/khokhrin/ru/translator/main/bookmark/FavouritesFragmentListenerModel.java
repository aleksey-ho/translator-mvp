package aleksey.khokhrin.ru.translator.main.bookmark;

public class FavouritesFragmentListenerModel {
    public interface FavouritesFragmentListener {
        void updateFavouritesTab();
    }

    private static FavouritesFragmentListenerModel mInstance;
    private FavouritesFragmentListener mListener;

    private FavouritesFragmentListenerModel() {}

    public static FavouritesFragmentListenerModel getInstance() {
        if(mInstance == null) {
            mInstance = new FavouritesFragmentListenerModel();
        }
        return mInstance;
    }

    public void setListener(FavouritesFragmentListener listener) {
        mListener = listener;
    }

    public void updateFavouritesTab() {
        if(mListener != null) {
            mListener.updateFavouritesTab();
        }
    }

}
