package aleksey.khokhrin.ru.translator;

import com.facebook.stetho.Stetho;

public class TranslatorDebugApplication extends TranslatorApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
	
}
