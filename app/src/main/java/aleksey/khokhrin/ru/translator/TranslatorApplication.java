package aleksey.khokhrin.ru.translator;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.squareup.leakcanary.LeakCanary;

import aleksey.khokhrin.ru.translator.data.source.DaggerRepositoryComponent;
import aleksey.khokhrin.ru.translator.data.source.Repository;
import aleksey.khokhrin.ru.translator.data.source.RepositoryComponent;
import aleksey.khokhrin.ru.translator.data.source.RepositoryModule;
import aleksey.khokhrin.ru.translator.data.source.remote.YandexTranslateApiModule;
import aleksey.khokhrin.ru.translator.util.SharedPreferenceHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static aleksey.khokhrin.ru.translator.util.SharedPreferenceHelper.FIRST_APP_LAUNCH_PREF;

public class TranslatorApplication extends Application {
    private static final String TAG = TranslatorApplication.class.getSimpleName();

    private RepositoryComponent mRepositoryComponent;
    private Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            return;
        }
        LeakCanary.install(this);

        mRepositoryComponent = DaggerRepositoryComponent.builder()
                .applicationModule(new ApplicationModule((getApplicationContext())))
                .yandexTranslateApiModule(new YandexTranslateApiModule())
                .repositoryModule(new RepositoryModule())
                .build();

        repository = mRepositoryComponent.getRepository();

        //indicate first launch
        boolean isFirstLaunch = SharedPreferenceHelper.getInstance().getBooleanValue(this, FIRST_APP_LAUNCH_PREF, true);
        if (isFirstLaunch) {
            repository.initLanguages().blockingGet();
            SharedPreferenceHelper.getInstance().saveBoolean(this, false, FIRST_APP_LAUNCH_PREF);
        }

        loadLanguages();
    }

    private void loadLanguages() {
        repository.loadLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("languagesUpdated"));
                }, Throwable::printStackTrace);
    }

    public RepositoryComponent getRepositoryComponent() {
        return mRepositoryComponent;
    }

}
