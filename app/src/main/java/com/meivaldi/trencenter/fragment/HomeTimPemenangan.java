package com.meivaldi.trencenter.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.meivaldi.trencenter.R;
import com.meivaldi.trencenter.activity.LogistikActivity;
import com.meivaldi.trencenter.activity.ProgramKerja;
import com.meivaldi.trencenter.activity.pendukung.InputPendukung;
import com.meivaldi.trencenter.activity.relawan.InputRelawan;
import com.meivaldi.trencenter.activity.tim_pemenangan.ProgramKerja_TimPemenangan;
import com.meivaldi.trencenter.activity.tim_pemenangan.Tim_Pemenangan;
import com.meivaldi.trencenter.adapter.CardAdapter;
import com.meivaldi.trencenter.adapter.SliderPagerAdapter;
import com.meivaldi.trencenter.adapter.ViewPagerAdapter;
import com.meivaldi.trencenter.helper.FragmentSlider;
import com.meivaldi.trencenter.helper.HttpHandler;
import com.meivaldi.trencenter.helper.SliderIndicator;
import com.meivaldi.trencenter.helper.SliderUtils;
import com.meivaldi.trencenter.helper.SliderView;
import com.meivaldi.trencenter.model.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeTimPemenangan extends Fragment  {

    private TextView hari, detik, menit, jam, selanjutnya;
    private FloatingActionButton create;

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<Card> cardList;

    private static final String TAG = HomeTimPemenangan.class.getSimpleName();
    private static final String url = "http://103.28.53.181/~millenn1/android/getCard.php";

    Dialog dialog;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private RequestQueue rq;
    private List<SliderUtils> sliderImg;
    private RelativeLayout logistik;

    String request_url = "http://103.28.53.181/~millenn1/android/debug.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_home_tim_pemenangan, container, false);

        rootView.setAlpha(0f);
        rootView.setVisibility(View.GONE);

        hari = (TextView) rootView.findViewById(R.id.hari);
        jam = (TextView) rootView.findViewById(R.id.jam);
        menit = (TextView) rootView.findViewById(R.id.menit);
        detik = (TextView) rootView.findViewById(R.id.detik);
        selanjutnya = (TextView) rootView.findViewById(R.id.selanjutnya);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        logistik = (RelativeLayout) rootView.findViewById(R.id.logistik);

        logistik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LogistikActivity.class));
            }
        });

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        new GetCards().execute();

        rq = Volley.newRequestQueue(getContext());
        sliderImg = new ArrayList<>();

        sendRequest();

        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(), 2000, 4000);

        selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProgramKerja_TimPemenangan.class));
            }
        });

        create = (FloatingActionButton) rootView.findViewById(R.id.createUser);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.input_data);
                dialog.setCancelable(true);

                RelativeLayout inputRelawan = (RelativeLayout) dialog.findViewById(R.id.relawan);
                RelativeLayout inputPendukung = (RelativeLayout) dialog.findViewById(R.id.pendukung);

                inputRelawan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getContext(), InputRelawan.class));
                        dialog.dismiss();
                    }
                });

                inputPendukung.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getContext(), InputPendukung.class));
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        countDown();

        rootView.setVisibility(View.VISIBLE);
        rootView.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(null);

        return rootView;
    }

    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(request_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String url = "http://103.28.53.181/~millenn1/dashboard/save/foto_berita/";
                List<String> headlineList = new ArrayList<>();

                for (int i=0; i<response.length(); i++){
                    SliderUtils sliderUtils = new SliderUtils();
                   try {
                       JSONArray array = response.getJSONArray(i);
                       String image = url + array.getString(0);
                       headlineList.add(array.getString(1));

                       sliderUtils.setSliderImageUrl(image);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                    sliderImg.add(sliderUtils);
                }

                adapter = new ViewPagerAdapter(sliderImg, headlineList, getContext());
                viewPager.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        rq.add(jsonArrayRequest);
    }

    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {

            if(getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    } else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    } else if(viewPager.getCurrentItem() == 2){
                        viewPager.setCurrentItem(3);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void countDown(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String dateStart = format.format(c);
        String dateEnd = "04/17/2019 06:00:00";

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        hari.setText("" + diffDays);
        jam.setText("" + diffHours);
        menit.setText("" + diffMinutes);
        detik.setText("" + diffSeconds);

        new CountDownTimer(diffSeconds * 1000, 1000){
            @Override
            public void onTick(long l) {
                detik.setText("" + l / 1000);
            }

            @Override
            public void onFinish() {
                countDown();
            }
        }.start();

    }

    private class GetCards extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cardList = new ArrayList<>();
            cardAdapter = new CardAdapter(getContext(), cardList);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray programs = jsonObj.getJSONArray("cards");

                    for (int i = 0; i < programs.length(); i++) {
                        JSONArray program = programs.getJSONArray(i);

                        String nama = program.getString(1);
                        String tanggalMulai = program.getString(2);
                        String foto = program.getString(7);

                        cardList.add(new Card(nama, tanggalMulai, foto));
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(isAdded()){
                getResources().getString(R.string.app_name);
            }

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cardAdapter);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
