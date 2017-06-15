package aleksey.khokhrin.ru.translator.data.source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class RepositoryTest {

    private Repository mRepository;

    @Mock
    private DataSource mRemoteDataSource;

    @Mock
    private DataSource mLocalDataSource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mRepository = new Repository(mRemoteDataSource, mLocalDataSource);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getTranslate() throws Exception {

    }

    @Test
    public void getLanguages() throws Exception {

    }

    @Test
    public void loadRemoteLanguages() throws Exception {

    }

    @Test
    public void loadLanguages() throws Exception {

    }

    @Test
    public void getRecentlyUsedSourceLanguage() throws Exception {

    }

    @Test
    public void getRecentlyUsedTargetLanguage() throws Exception {

    }

    @Test
    public void getRecentlyUsedSourceLanguages() throws Exception {

    }

    @Test
    public void getRecentlyUsedTargetLanguages() throws Exception {

    }

    @Test
    public void updateLanguages() throws Exception {

    }

    @Test
    public void initLanguages() throws Exception {

    }

    @Test
    public void updateLanguageUsage() throws Exception {

    }

    @Test
    public void addTranslate() throws Exception {

    }

    @Test
    public void getTranslatesInHistory() throws Exception {

    }

    @Test
    public void getFavouriteTranslates() throws Exception {

    }

    @Test
    public void saveAsFavourite() throws Exception {

    }

    @Test
    public void removeFromFavourites() throws Exception {

    }

    @Test
    public void clearHistory() throws Exception {

    }

    @Test
    public void clearFavourites() throws Exception {

    }

    @Test
    public void initFirstSelectedLanguages() throws Exception {

    }

}