package ua.kpi.comsys.IO8123.ui.book;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import ua.kpi.comsys.IO8123.R;

public class BookAddView {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    public Object[] showPopupWindow(final View view) {
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View popupView = inflater.inflate(R.layout.book_add, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();

            popupView.getWindowVisibleDisplayFrame(r);

            int heightDiff = popupView.getRootView().getHeight() - (r.bottom - r.top);
            focusChange(popupView, heightDiff > 100);
            if (heightDiff > 100) {
            }
        });

        return new Object[] {popupView, popupWindow};
    }

    private void focusChange(View popupView, boolean hasFocus){
        CardView parent = popupView.findViewById(R.id.card_add);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)parent.getLayoutParams();

        if (hasFocus)
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        else
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;

        parent.setLayoutParams(params);
    }
}