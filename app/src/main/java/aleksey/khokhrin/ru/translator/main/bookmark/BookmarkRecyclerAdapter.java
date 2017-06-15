package aleksey.khokhrin.ru.translator.main.bookmark;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.data.Translate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = BookmarkRecyclerAdapter.class.getSimpleName();

    BookmarkListener bookmarkListener;

    private List<Translate> items = new ArrayList<>();
    private Context context;

    public BookmarkRecyclerAdapter(Context context, List<Translate> items, BookmarkListener bookmarkListener) {
        this.bookmarkListener = bookmarkListener;
        this.context = context;
        if (items != null)
            this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ItemBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemBookmarkViewHolder)holder).bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void setSavedAsFavourite(Translate translate) {
        for (Translate translate1 : items) {
            if(Translate.equals(translate, translate1)) {
                translate1.setSavedInFavourites(true);
                notifyItemChanged(items.indexOf(translate1));
                break;
            }
        }
    }

    void setRemovedFromFavourites(Translate translate) {
        for (Translate translate1 : items) {
            if(Translate.equals(translate, translate1)) {
                translate1.setSavedInFavourites(false);
                notifyItemChanged(items.indexOf(translate1));
                break;
            }
        }
    }

    class ItemBookmarkViewHolder extends RecyclerView.ViewHolder {

        Translate translate;

        @BindView(R.id.textSource) TextView textSource;
        @BindView(R.id.textTarget) TextView textTarget;
        @BindView(R.id.imageFavourite) ImageView imageFavourite;
        @BindView(R.id.textViewLanguageSourceCode) TextView textViewLanguageSourceCode;
        @BindView(R.id.textViewLanguageTargetCode) TextView textViewLanguageTargetCode;

        @OnClick(R.id.imageFavourite)
        void saveAsFavouriteClick() {
            if (translate.isSavedInFavourites())
                bookmarkListener.removeFromFavourites(translate);
            else
                bookmarkListener.saveAsFavourite(translate);
        }

        @OnClick(R.id.linearLayoutShowItem)
        void onShowClick() {
            bookmarkListener.openTranslate(translate);
        }

        ItemBookmarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Translate translate) {
            this.translate = translate;

            textSource.setText(translate.getTextSource());
            textTarget.setText(translate.getTextTarget());

            int retValue = translate.isSavedInFavourites() ? R.drawable.ic_favourive_light : R.drawable.ic_favourite_dark;
            imageFavourite.setBackground(ContextCompat.getDrawable(context, retValue));

            textViewLanguageSourceCode.setText(translate.getLanguageSource().getCode().toUpperCase());
            textViewLanguageTargetCode.setText(translate.getLanguageTarget().getCode().toUpperCase());

        }
    }

}
