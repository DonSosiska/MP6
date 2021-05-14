package  ua.kpi.comsys.IO8123.ui.book;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BookViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Books");
    }

    public LiveData<String> getText() {
        return mText;
    }
}