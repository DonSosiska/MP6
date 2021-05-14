package  ua.kpi.comsys.IO8123.ui.book;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ua.kpi.comsys.IO8123.R;

public class BookFragment extends Fragment {

    private static HashMap<ConstraintLayout, Book> booksMap;
    protected static View root;
    private static LinearLayout bookList;
    private static LinearLayout noItems;
    private static ProgressBar loadingBar;
    private static TextView noItemsText;
    private static Set<ConstraintLayout> removeSet;

    private static int backgroundResource;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_book, container, false);

        setRetainInstance(true);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
        backgroundResource = typedArray.getResourceId(0, 0);

        bookList = root.findViewById(R.id.book_linear_scrolable);
        booksMap = new HashMap<>();

        SearchView simpleSearchView = root.findViewById(R.id.btn_search);
        noItems = root.findViewById(R.id.no_items_view);
        loadingBar = root.findViewById(R.id.no_items_progressbar);
        noItemsText = root.findViewById(R.id.no_items_text);

        removeSet = new HashSet<>();

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                removeSet.addAll(booksMap.keySet());
                if (newText.length() >= 3) {
                    AsyncLoadBooks aTask = new AsyncLoadBooks();
                    noItems.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.VISIBLE);
                    noItemsText.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newText);
                }
                else {
                    for (ConstraintLayout constraintLayout : removeSet) {
                        binClicked(constraintLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                removeSet.addAll(booksMap.keySet());
                if (newText.length() >= 3) {
                    AsyncLoadBooks aTask = new AsyncLoadBooks();
                    noItems.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.VISIBLE);
                    noItemsText.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newText);
                }
                else {
                    for (ConstraintLayout constraintLayout : removeSet) {
                        binClicked(constraintLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }
        });

        ImageButton btnAddBook = root.findViewById(R.id.btn_add_book);
        btnAddBook.setOnClickListener(v -> {
            BookAddView popUpClass = new BookAddView();
            Object[] popups = popUpClass.showPopupWindow(v);

            View popupView = (View) popups[0];
            PopupWindow popupWindow = (PopupWindow) popups[1];

            EditText inputTitle = popupView.findViewById(R.id.input_title);
            EditText inputSubtitle = popupView.findViewById(R.id.input_subtitle);
            EditText inputPrice = popupView.findViewById(R.id.input_price);

            Button buttonAdd = popupView.findViewById(R.id.button_add_add);
            buttonAdd.setOnClickListener(v1 -> {
                if (inputTitle.getText().toString().length() != 0 &&
                        inputSubtitle.getText().toString().length() != 0 &&
                        inputPrice.getText().toString().length() != 0) {

                    addNewBook(new Book(inputTitle.getText().toString(),
                            inputSubtitle.getText().toString(),
                            inputPrice.getText().toString()));
                    changeLaySizes();
                    noItems.setVisibility(View.GONE);

                    popupWindow.dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "You must fill all fields!",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        changeLaySizes();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected static void loadBooks(ArrayList<Book> books){
        if (books != null) {
            if (books.size() > 0) {
                noItems.setVisibility(View.GONE);
                for (Book book :
                        books) {
                    addNewBook(book);
                }
                for (ConstraintLayout constraintLayout : removeSet) {
                    binClicked(constraintLayout);
                }
                removeSet.clear();
            } else {
                noItems.setVisibility(View.VISIBLE);
            }
        }
        else
            Toast.makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_LONG).show();
        loadingBar.setVisibility(View.GONE);
        noItemsText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeLaySizes();
    }

    private void changeLaySizes(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) root.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (ConstraintLayout bookshelf :
                booksMap.keySet()) {
            bookshelf.getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(width/3, width/3));
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void addNewBook(Book book){
        SwipeLayout swipeLay = new SwipeLayout(root.getContext());
        swipeLay.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLay.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
        bookList.addView(swipeLay);

        ImageButton btnBin = new ImageButton(root.getContext());
        btnBin.setPadding(50, 0, 50, 0);
        btnBin.setImageResource(R.drawable.ic_baseline_delete_forever_24);
        btnBin.setBackgroundResource(R.drawable.outline_bin);

        btnBin.setForeground(root.getContext().getDrawable(backgroundResource));

        LinearLayout.LayoutParams btnBinParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        btnBinParams.gravity = Gravity.RIGHT;
        swipeLay.addDrag(SwipeLayout.DragEdge.Right, btnBin);
        swipeLay.addView(btnBin, 0, btnBinParams);

        ConstraintLayout bookLayTmp = new ConstraintLayout(root.getContext());
        bookLayTmp.setBackgroundResource(R.drawable.color_outline);

        bookLayTmp.setForeground(root.getContext().getDrawable(backgroundResource));

        bookLayTmp.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        swipeLay.addView(bookLayTmp, 1);

        btnBin.setOnClickListener(v -> binClicked(bookLayTmp));

        bookLayTmp.setOnClickListener(v -> {
            if (book.getIsbn13().length() != 0 && !book.getIsbn13().equals("noid")) {
                BookInfoView popUpClass = new BookInfoView();
                popUpClass.showPopupWindow(v, book);
            }
        });

        ProgressBar loadingImageBar = new ProgressBar(root.getContext());
        loadingImageBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(root.getContext(), R.color.purple_500),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingImageBar.setVisibility(View.GONE);
        loadingImageBar.setId(loadingImageBar.hashCode());
        bookLayTmp.addView(loadingImageBar);

        ImageView imageTmp = new ImageView(root.getContext());
        imageTmp.setId(imageTmp.hashCode());
        ConstraintLayout.LayoutParams imgParams =
                new ConstraintLayout.LayoutParams(300, 300);
        imageTmp.setImageResource(R.drawable.no_image);
        if (book.getImagePath().length() != 0){
            imageTmp.setVisibility(View.INVISIBLE);
            loadingImageBar.setVisibility(View.VISIBLE);
            new DownloadImageTask(imageTmp, loadingImageBar, root.getContext()).execute(book.getImagePath());
        }
        bookLayTmp.addView(imageTmp, 0, imgParams);

        ConstraintLayout textConstraint = new ConstraintLayout(root.getContext());
        textConstraint.setId(textConstraint.hashCode());
        bookLayTmp.addView(textConstraint, 1);

        TextView textTitle = new TextView(root.getContext());
        textTitle.setId(textTitle.hashCode());
        textTitle.setPadding(0, 1, 5, 1);
        textTitle.setText(book.getTitle());
        ConstraintLayout.LayoutParams textTitleParams =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textConstraint.addView(textTitle, 0, textTitleParams);

        TextView textSubtitle = new TextView(root.getContext());
        textSubtitle.setText(book.getSubtitle());
        textSubtitle.setPadding(0, 1, 5, 1);
        textSubtitle.setId(textSubtitle.hashCode());
        ConstraintLayout.LayoutParams textSubtitleParams =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textConstraint.addView(textSubtitle, 1, textSubtitleParams);

        TextView textPrice = new TextView(root.getContext());
        textPrice.setText(book.getPrice());
        textPrice.setPadding(0, 0, 5, 4);
        textPrice.setId(textPrice.hashCode());
        ConstraintLayout.LayoutParams textPriceParams =
                new ConstraintLayout.LayoutParams(ConstraintSet.WRAP_CONTENT,
                        ConstraintSet.WRAP_CONTENT);
        textConstraint.addView(textPrice, 2, textPriceParams);

        ConstraintSet textConstraintSet = new ConstraintSet();
        textConstraintSet.clone(textConstraint);

        textConstraintSet.connect(textTitle.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        textConstraintSet.connect(textTitle.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        textConstraintSet.connect(textSubtitle.getId(), ConstraintSet.TOP,
                textTitle.getId(), ConstraintSet.BOTTOM);
        textConstraintSet.connect(textSubtitle.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        textConstraintSet.connect(textSubtitle.getId(), ConstraintSet.BOTTOM,
                textPrice.getId(), ConstraintSet.TOP);
        textConstraintSet.connect(textPrice.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        textConstraintSet.connect(textPrice.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);

        textConstraintSet.setVerticalBias(textSubtitle.getId(), 0.3f);

        textConstraintSet.setMargin(textSubtitle.getId(), ConstraintSet.TOP, 3);
        textConstraintSet.setMargin(textSubtitle.getId(), ConstraintSet.BOTTOM, 3);

        textConstraintSet.applyTo(textConstraint);

        ConstraintSet bookLayTmpSet = new ConstraintSet();
        bookLayTmpSet.clone(bookLayTmp);

        bookLayTmpSet.connect(imageTmp.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        bookLayTmpSet.connect(imageTmp.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        bookLayTmpSet.connect(imageTmp.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        bookLayTmpSet.connect(textConstraint.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        bookLayTmpSet.connect(textConstraint.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        bookLayTmpSet.connect(textConstraint.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        bookLayTmpSet.connect(textConstraint.getId(), ConstraintSet.START,
                imageTmp.getId(), ConstraintSet.END);

        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.START,
                imageTmp.getId(), ConstraintSet.START);
        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.TOP,
                imageTmp.getId(), ConstraintSet.TOP);
        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.END,
                imageTmp.getId(), ConstraintSet.END);
        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.BOTTOM,
                imageTmp.getId(), ConstraintSet.BOTTOM);

        bookLayTmpSet.constrainWidth(textConstraint.getId(), ConstraintSet.MATCH_CONSTRAINT);
        bookLayTmpSet.constrainHeight(textConstraint.getId(), ConstraintSet.MATCH_CONSTRAINT);

        bookLayTmpSet.applyTo(bookLayTmp);

        booksMap.put(bookLayTmp, book);
    }

    private static void binClicked(ConstraintLayout key){
        booksMap.remove(key);
        //((SwipeLayout) key.getParent()).removeAllViewsInLayout();
        bookList.removeView(((SwipeLayout) key.getParent()));
        if (booksMap.keySet().isEmpty()){
            noItems.setVisibility(View.VISIBLE);
            noItemsText.setVisibility(View.VISIBLE);
        }
    }

    private static class AsyncLoadBooks extends AsyncTask<String, Void, ArrayList<Book>> {
        private String getRequest(String url){
            StringBuilder result = new StringBuilder();
            try {
                URL getReq = new URL(url);
                URLConnection bookConnection = getReq.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(bookConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();

            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", url));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        private ArrayList<Book> parseBooks(String jsonText) throws ParseException {
            ArrayList<Book> result = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            JSONArray books = (JSONArray) jsonObject.get("books");
            for (Object book : books) {
                JSONObject tmp = (JSONObject) book;
                result.add(new Book(
                        (String) tmp.get("title"),
                        (String) tmp.get("subtitle"),
                        (String) tmp.get("isbn13"),
                        (String) tmp.get("price"),
                        (String) tmp.get("image")
                ));
            }

            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private ArrayList<Book> search(String newText){
            String jsonResponse = String.format("https://api.itbook.store/1.0/search/\"%s\"", newText);
            try {
                ArrayList<Book> books = parseBooks(getRequest(jsonResponse));
                return books;
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected ArrayList<Book> doInBackground(String... strings) {
            return search(strings[0]);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            super.onPostExecute(books);
            BookFragment.loadBooks(books);
        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;
        @SuppressLint("StaticFieldLeak")
        ProgressBar loadingBar;
        @SuppressLint("StaticFieldLeak")
        Context context;

        public DownloadImageTask(ImageView bmImage, ProgressBar loadingBar, Context context) {
            this.bmImage = bmImage;
            this.loadingBar = loadingBar;
            this.context = context;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                bmImage.setImageBitmap(result);
            else {
                bmImage.setBackgroundResource(R.drawable.no_image);
                Toast.makeText(context, "Cannot load data!", Toast.LENGTH_LONG).show();
            }
            loadingBar.setVisibility(View.GONE);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}