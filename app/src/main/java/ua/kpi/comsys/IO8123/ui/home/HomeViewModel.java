package ua.kpi.comsys.IO8123.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Савотиков Игорь\nГруппа ИО-81\nЗК ІО-8123");
    }

    public LiveData<String> getText() {
        return mText;
    }
}