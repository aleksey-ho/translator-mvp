package aleksey.khokhrin.ru.translator.langSelector;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.data.Language;

import static aleksey.khokhrin.ru.translator.langSelector.LangSelectionAdapter.LangSelectionAdapterType.TYPE_ITEM;
import static aleksey.khokhrin.ru.translator.langSelector.LangSelectionAdapter.LangSelectionAdapterType.TYPE_SEPARATOR;

public class LangSelectionAdapter extends BaseAdapter {
    private List<Object> items = new ArrayList<>();
    private List<Integer> sectionHeader = new ArrayList<>();

    private Context context;

    public LangSelectionAdapter(Context context) {
        this.context = context;
    }

    public void addItem(final Language item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addItems(final List<Language> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        items.add(item);
        sectionHeader.add(items.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR.getValue() : TYPE_ITEM.getValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);

        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);

        switch (LangSelectionAdapterType.values()[getItemViewType(position)]) {
            case TYPE_ITEM:
                text1.setText(((Language)items.get(position)).getName());
                break;
            case TYPE_SEPARATOR:
                text1.setText(((String)items.get(position)).toUpperCase());
                text1.setTextColor(ContextCompat.getColor(context, R.color.colorListViewTextSeparator));
                break;
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    enum LangSelectionAdapterType {
        TYPE_ITEM(0), TYPE_SEPARATOR(1);
        private int value;
        LangSelectionAdapterType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

}
