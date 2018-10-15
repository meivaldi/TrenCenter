package com.bmc.trencenter.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bmc.trencenter.R;
import com.bmc.trencenter.activity.Berita;
import com.bmc.trencenter.activity.LayananActivity;
import com.bmc.trencenter.activity.LogistikActivity;
import com.bmc.trencenter.activity.Partnership;
import com.bmc.trencenter.activity.pendukung.InputPendukung;
import com.bmc.trencenter.activity.relawan.InputRelawan;
import com.bmc.trencenter.activity.tim_pemenangan.ProgramKerja_TimPemenangan;
import com.bmc.trencenter.adapter.CardAdapter;
import com.bmc.trencenter.adapter.CardLogistik;
import com.bmc.trencenter.adapter.LayananAdapter;
import com.bmc.trencenter.adapter.PartnershipPemenanganAdapter;
import com.bmc.trencenter.adapter.ViewPagerAdapter;
import com.bmc.trencenter.app.AppConfig;
import com.bmc.trencenter.app.AppController;
import com.bmc.trencenter.helper.SliderUtils;
import com.bmc.trencenter.model.Card;

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

public class HomeTimPemenangan extends Fragment {

    private TextView hari, detik, menit, jam, selanjutnya, selanjutnya2, selanjutnya3, selanjutnya4, seeBerita;
    private FloatingActionButton create;

    private RecyclerView recyclerView, logistikRecycler, layananRecycler, partnershipRecycler;
    private CardAdapter cardAdapter;
    private CardLogistik logistikAdapter;
    private LayananAdapter layananAdapter;
    private PartnershipPemenanganAdapter partnershipAdapter;
    private List<Card> cardList, logistikList, layananList, partnershipList;

    private Dialog dialog;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private TextView emptyLayanan, emptyProgram, emptyLogistik, emptyPartnership;

    private RequestQueue rq;
    private List<SliderUtils> sliderImg;
    private SwipeRefreshLayout swipeRefreshLayout;

    String request_url = "http://156.67.221.225/voting/android/debug.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tim_pemenangan, container, false);

        rootView.setAlpha(0f);
        rootView.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        hari = (TextView) rootView.findViewById(R.id.hari);
        jam = (TextView) rootView.findViewById(R.id.jam);
        menit = (TextView) rootView.findViewById(R.id.menit);
        detik = (TextView) rootView.findViewById(R.id.detik);
        selanjutnya = (TextView) rootView.findViewById(R.id.selanjutnya);
        selanjutnya2 = (TextView) rootView.findViewById(R.id.selanjutnya2);
        selanjutnya3 = (TextView) rootView.findViewById(R.id.selanjutnya3);
        selanjutnya4 = (TextView) rootView.findViewById(R.id.selanjutnya4);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        logistikRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_logistik);
        layananRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_layanan);
        partnershipRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_partnership);
        seeBerita = (TextView) rootView.findViewById(R.id.seeBerita);

        seeBerita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Berita.class));
            }
        });

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        layananList = new ArrayList<>();
        layananAdapter = new LayananAdapter(getContext(), layananList);
        logistikList = new ArrayList<>();
        logistikAdapter = new CardLogistik(getContext(), logistikList);
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(getContext(), cardList);
        partnershipList = new ArrayList<>();
        partnershipAdapter = new PartnershipPemenanganAdapter(getContext(), partnershipList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        partnershipRecycler.setLayoutManager(layoutManager);
        partnershipRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        partnershipRecycler.setItemAnimator(new DefaultItemAnimator());
        partnershipRecycler.setAdapter(partnershipAdapter);

        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(getContext(), 2);
        layananRecycler.setLayoutManager(layoutManager1);
        layananRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        layananRecycler.setItemAnimator(new DefaultItemAnimator());
        layananRecycler.setAdapter(layananAdapter);

        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(getContext(), 2);
        logistikRecycler.setLayoutManager(layoutManager2);
        logistikRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        logistikRecycler.setItemAnimator(new DefaultItemAnimator());
        logistikRecycler.setAdapter(logistikAdapter);

        RecyclerView.LayoutManager layoutManager3 = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager3);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);

        emptyLayanan = (TextView) rootView.findViewById(R.id.emptyLayanan);
        emptyLogistik = (TextView) rootView.findViewById(R.id.emptyLogistik);
        emptyProgram = (TextView) rootView.findViewById(R.id.emptyKegiatan);
        emptyPartnership = (TextView) rootView.findViewById(R.id.emptyPartnership);

        getCards();
        getLayanan();
        getLogistik();
        getPartnership();

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

        selanjutnya2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LayananActivity.class));
            }
        });

        selanjutnya3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LogistikActivity.class));
            }
        });

        selanjutnya4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Partnership.class));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, this)
                .commit();
    }

    public void sendRequest() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(request_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String url = "http://156.67.221.225/voting/dashboard/save/foto_berita/";
                List<String> headlineList = new ArrayList<>();
                List<String> sourceList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    SliderUtils sliderUtils = new SliderUtils();
                    try {
                        JSONArray array = response.getJSONArray(i);

                        String foto = array.getString(7);
                        String tes = "";

                        for(int j=0; j<foto.length(); j++){
                            if(foto.charAt(j) == ' '){
                                tes += "%20";
                            } else {
                                tes += foto.charAt(j);
                            }
                        }

                        String image = url + tes;
                        headlineList.add(array.getString(3));
                        sourceList.add(array.getString(8));

                        sliderUtils.setSliderImageUrl(image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);
                }

                adapter = new ViewPagerAdapter(sliderImg, headlineList, sourceList, getContext());
                viewPager.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        rq.add(jsonArrayRequest);
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = 0;

                    try {
                        length = adapter.getCount();
                    } catch (NullPointerException e) {
                        e.printStackTrace();

                    }

                    if (length == 2) {
                        if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (viewPager.getCurrentItem() == 1) {
                            viewPager.setCurrentItem(0);
                        }
                    } else if (length == 3) {
                        if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (viewPager.getCurrentItem() == 1) {
                            viewPager.setCurrentItem(2);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    } else if (length == 4) {
                        if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (viewPager.getCurrentItem() == 1) {
                            viewPager.setCurrentItem(2);
                        } else if (viewPager.getCurrentItem() == 2) {
                            viewPager.setCurrentItem(3);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
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

    private int dpToPx(int dp) throws IllegalStateException {
        Resources r = getActivity().getResources();

        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void countDown() {
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

        new CountDownTimer(diffSeconds * 1000, 1000) {
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

    private void getLayanan() {
        String tag_string_req = "req_layanan";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_LAYANAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("LAYANAN", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray programs = jObj.getJSONArray("layanan");

                        for (int i = 0; i < programs.length(); i++) {
                            JSONArray program = programs.getJSONArray(i);

                            if(program.length() == 0){
                                emptyLayanan.setVisibility(View.VISIBLE);
                            } else {
                                emptyLayanan.setVisibility(View.GONE);
                            }

                            String nama = program.getString(1);
                            String tanggalMulai = program.getString(2);
                            String foto = "http://156.67.221.225/voting/dashboard/save/foto_layanan/" + program.getString(5);

                            layananList.add(new Card(nama, tanggalMulai, foto));
                        }

                        layananAdapter.notifyDataSetChanged();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String errormsg = "Tidak Ada Koneksi Internet!";
                Toast.makeText(getActivity().getApplicationContext(), errormsg, Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getLogistik() {
        String tag_string_req = "req_logistik";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_LOGISTIC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("LOGISTIK", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray programs = jObj.getJSONArray("logistik");

                        if(programs.length() == 0){
                            emptyLogistik.setVisibility(View.VISIBLE);
                        } else {
                            emptyLogistik.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < programs.length(); i++) {
                            JSONArray program = programs.getJSONArray(i);

                            String nama = program.getString(1);
                            String tanggalMulai = program.getString(2);
                            String foto = "http://156.67.221.225/voting/dashboard/save/foto_logistik/" + program.getString(7);

                            logistikList.add(new Card(nama, tanggalMulai, foto));
                        }

                        logistikAdapter.notifyDataSetChanged();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Koneksi lambat", Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getCards() {
        String tag_string_req = "req_card";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_CARD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("PROGRAM", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        JSONArray programs = jObj.getJSONArray("cards");

                        if(programs.length() == 0){
                            emptyProgram.setVisibility(View.VISIBLE);
                        } else {
                            emptyProgram.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < programs.length(); i++) {
                            JSONArray program = programs.getJSONArray(i);

                            String nama = program.getString(1);
                            String tanggalMulai = program.getString(2);
                            String foto = "http://156.67.221.225/voting/dashboard/save/foto_program/" + program.getString(7);

                            cardList.add(new Card(nama, tanggalMulai, foto));
                        }

                        cardAdapter.notifyDataSetChanged();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errormsg = "Tidak Ada Koneksi Internet!";
                Toast.makeText(getActivity().getApplicationContext(), errormsg, Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getPartnership() {
        String tag_string_req = "req_partnership";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_PARTNERSHIP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("PARTNERSHIP", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        JSONArray programs = jObj.getJSONArray("partnership");

                        if(programs.length() == 0){
                            emptyPartnership.setVisibility(View.VISIBLE);
                        } else {
                            emptyPartnership.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < programs.length(); i++) {
                            JSONArray program = programs.getJSONArray(i);

                            String nama = program.getString(1);
                            String tanggalMulai = program.getString(2);
                            String foto = "http://156.67.221.225/voting/dashboard/save/foto_partnership/" + program.getString(7);

                            partnershipList.add(new Card(nama, tanggalMulai, foto));
                        }

                        partnershipAdapter.notifyDataSetChanged();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Activity activity = getActivity();
                if (activity != null && isAdded()) {
                    if (error instanceof NoConnectionError) {
                        String errormsg = "Tidak Ada Koneksi Internet!";
                        Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                    } else {
                        String errormsg = "Tidak Ada Koneksi Internet!";
                        Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}