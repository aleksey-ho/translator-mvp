package aleksey.khokhrin.ru.translator.main.transtlate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.data.LangSelectionDirection;
import aleksey.khokhrin.ru.translator.data.Language;
import aleksey.khokhrin.ru.translator.data.Translate;
import aleksey.khokhrin.ru.translator.langSelector.LangSelectionActivity;
import aleksey.khokhrin.ru.translator.langSelector.LangSelectionListenerModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.adapter.rxjava2.HttpException;

public class TranslateFragment extends Fragment
        implements TranslateContract.View, LangSelectionListenerModel.LangSelectionListener {
    public static final String TAG = TranslateFragment.class.getSimpleName();

    public static final String TEXT_SOURCE = "TEXT_SOURCE";
    public static final String TEXT_TRANSLATE = "TEXT_TRANSLATE";

    @Inject
    TranslatePresenter translatePresenter;

    @BindView(R.id.textViewLangSource) TextView textViewLangSource;
    @BindView(R.id.textViewLangTarget) TextView textViewLangTarget;
    @BindView(R.id.editTextToTranslate) EditText editTextToTranslate;
    @BindView(R.id.textViewTranslate) TextView textViewTranslate;
    @BindView(R.id.internetConnectionError) LinearLayout internetConnectionError;

    @OnClick(R.id.buttonTryAgain)
    void tryAgainButtonClick() {
        translatePresenter.translateText(editTextToTranslate.getText().toString());
    }

    @OnClick(R.id.buttonClear)
    void clearSourceText() {
        translatePresenter.clearSourceText();
    }

    @OnClick(R.id.buttonVoiceInput)
    void voiceInput() {
        Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.buttonPlayback)
    void playback() {
        Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.textViewLangSource)
    void onSelectSourceLanguageClick() {
        getContext().startActivity(LangSelectionActivity.newIntent(getContext(), LangSelectionDirection.SOURCE));
    }

    @OnClick(R.id.textViewLangTarget)
    void onSelectTargetLanguageClick() {
        getContext().startActivity(LangSelectionActivity.newIntent(getContext(), LangSelectionDirection.TARGET));
    }

    @OnClick(R.id.buttonSwap)
    void onSwapLanguagesClick() {
        translatePresenter.swapLanguages();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_translate, container, false);
        ButterKnife.bind(this, root);
        LangSelectionListenerModel.getInstance().setListener(this);

        editTextToTranslate.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editTextToTranslate.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            //when user stops typing and presses Enter
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (v.getText().length() > 0)
                    translatePresenter.translateText(editTextToTranslate.getText().toString());
                else {
                    clearTranslate();
                    clearSourceText();
                }
                handled = true;
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return handled;
        });

        //when user is typing - translate in real-time
        editTextToTranslate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                translatePresenter.translateText(editTextToTranslate.getText().toString(), false);
            }
        });

        //make it scrollable
        textViewTranslate.setMovementMethod(new ScrollingMovementMethod());

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("languagesUpdated"));

        return root;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            translatePresenter.loadLanguages();
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void clearTranslate() {
        translatePresenter.clearTranslate();
    }

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    public TranslateFragment() { }

    @Override
    public void onResume() {
        super.onResume();
        translatePresenter.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        translatePresenter.loadLanguages();

        if (savedInstanceState != null) {
            String textSource = savedInstanceState.getString(TEXT_SOURCE);
            translatePresenter.setSourceText(textSource);
            String translate = savedInstanceState.getString(TEXT_TRANSLATE);
            translatePresenter.setTranslate(translate);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setLanguageSource(String name) {
        textViewLangSource.setText(name);
    }

    @Override
    public void setLanguageTarget(String name) {
        textViewLangTarget.setText(name);
    }

    @Override
    public void setTranslate(String translate) {
        textViewTranslate.setText(translate);
    }

    @Override
    public void setSourceText(String text) {
        editTextToTranslate.setText(text);
    }

    @Override
    public void hideInternetConnectionError() {
        internetConnectionError.setVisibility(View.GONE);
    }

    @Override
    public void showInternetConnectionError() {
        internetConnectionError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHttpException(HttpException exception) {
        AlertDialog.Builder messageBox = new AlertDialog.Builder(getContext());
        messageBox.setTitle(R.string.error_title);
        messageBox.setNegativeButton(R.string.ok, null);
        if (exception.code() == 413)
            messageBox.setMessage(R.string.error_text_length);
        else if (exception.code() == 501)
            messageBox.setMessage(R.string.error_translation_direction);
        else
            messageBox.setMessage(R.string.error_default);
        messageBox.show();
    }

    @Override
    public void langSelected(Language language, LangSelectionDirection direction) {
        if(direction == LangSelectionDirection.SOURCE)
            translatePresenter.setLanguageSource(language);
        else
            translatePresenter.setLanguageTarget(language);
        translatePresenter.translateText(editTextToTranslate.getText().toString());
    }

    public void saveTranslate() {
        translatePresenter.saveTranslate();
    }

    public void setTranslate(Translate translate) {
        translatePresenter.setLanguageSource(translate.getLanguageSource());
        translatePresenter.setLanguageTarget(translate.getLanguageTarget());
        translatePresenter.setSourceText(translate.getTextSource());
        translatePresenter.setTranslate(translate.getTextTarget());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(TEXT_SOURCE, editTextToTranslate.getText().toString());
        savedInstanceState.putString(TEXT_TRANSLATE, textViewTranslate.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }
}
