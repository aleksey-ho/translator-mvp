package aleksey.khokhrin.ru.translator.langSelector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.TranslatorApplication;
import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LangSelectionActivity extends AppCompatActivity implements LangSelectionContract.View {
    private static final String TAG = LangSelectionActivity.class.getSimpleName();
    public final static String DIRECTION = "DIRECTION";
    private LangSelectionDirection direction;

    @Inject
    LangSelectionPresenter langSelectionPresenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listViewLanguages)
    ListView listViewLanguages;

    public static Intent newIntent(Context context, LangSelectionDirection direction) {
        Intent intent = new Intent(context, LangSelectionActivity.class);
        intent.putExtra(DIRECTION, direction.getValue());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_selection);
        ButterKnife.bind(this);
        direction = LangSelectionDirection.values()[getIntent().getIntExtra(DIRECTION, 0)];

        // Create the presenter
        DaggerLangSelectionComponent.builder()
                .repositoryComponent(((TranslatorApplication)getApplicationContext()).getRepositoryComponent())
                .langSelectionPresenterModule(new LangSelectionPresenterModule(this))
                .build()
                .inject(this);

        langSelectionPresenter.setDirection(direction);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(direction == LangSelectionDirection.SOURCE ?
                getString(R.string.language_source) : getString(R.string.language_target));

        // создаем адаптер
        LangSelectionAdapter adapter = new LangSelectionAdapter(this);

        //добавляем в список недавно использованные языки (если есть)
        List<Language> recentlyUsedLangs = langSelectionPresenter.getRecentlyUsedSourceLangs();
        if (recentlyUsedLangs.size() > 0) {
            adapter.addSectionHeaderItem(getString(R.string.recently_used));
            adapter.addItems(recentlyUsedLangs);
        }

        //добавляем все языки
        List<Language> languages = langSelectionPresenter.getLanguages();
        if (languages.size() > 0) {
            adapter.addSectionHeaderItem(getString(R.string.all_languages));
            adapter.addItems(languages);
        }

        // присваиваем адаптер списку
        listViewLanguages.setAdapter(adapter);

        listViewLanguages.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getAdapter().getItem(position);
            if (!(item instanceof Language))
                return;
            Language language = (Language) item;
            LangSelectionListenerModel.getInstance().langSelected(language, direction);
            langSelectionPresenter.updateLanguageUsage(language, direction);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
