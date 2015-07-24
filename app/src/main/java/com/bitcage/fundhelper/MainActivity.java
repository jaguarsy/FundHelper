package com.bitcage.fundhelper;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bitcage.fundhelper.Models.FunderDetail;
import com.bitcage.fundhelper.Utils.AutoCompleteAdapter;
import com.bitcage.fundhelper.Utils.FundReader;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final int SET_SEARCH_DATA = 1;
    private static final int GET_FUNDER_DETAIL = 2;

    private ProgressDialog progressDialog;
    private AutoCompleteTextView acTextView;
    private TextView tvDate;
    private TextView tvUnitWorth;
    private TextView tvEstimateWorth;
    private TextView tvEstimateRiseRate;
    private TextView tvEstimateDate;
    private RelativeLayout detailRelativeLayout;
    private Button btSearch;
    private Button btClear;
    private ImageView imageView;

    private AutoCompleteAdapter autoCompleteAdapter;
    private InputMethodManager inputMethodManager;
    private FundReader reader;
    private String search_url = "http://fund.eastmoney.com/js/fundcode_search.js";
    private String detail_url = "http://fundgz.1234567.com.cn/js/%s.js";
    private String img_url = "http://j4.dfcfw.com/charts/pic1/%s.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("基金查询助手");

        acTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        acTextView.setOnItemClickListener(onItemClickListener);
        progressDialog = ProgressDialog.show(MainActivity.this, "正在加载基金数据...", "请稍候...", true, false);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvUnitWorth = (TextView) findViewById(R.id.tvUnitWorth);
        tvEstimateWorth = (TextView) findViewById(R.id.tvEstimateWorth);
        tvEstimateRiseRate = (TextView) findViewById(R.id.tvEstimateRiseRate);
        tvEstimateDate = (TextView) findViewById(R.id.tvEstimateDate);
        detailRelativeLayout = (RelativeLayout) findViewById(R.id.detailRelativeLayout);
        imageView = (ImageView) findViewById(R.id.imageView);

        btSearch = (Button) findViewById(R.id.btSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetail();
            }
        });

        btClear = (Button) findViewById(R.id.btClear);
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acTextView.setText("");
            }
        });

        inputMethodManager = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);

        getFunderListThread.start();
    }

    private String httpGet(String target_url) throws IOException {

        URL url = new URL(target_url);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            content.append(inputLine);
        in.close();

        return content.toString();
    }

    private Bitmap httpGetImage(String target_url) throws IOException {
        URL url = new URL(target_url);
        return BitmapFactory.decodeStream(url.openStream());
    }

    private Thread getFunderListThread = new Thread() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = SET_SEARCH_DATA;

            try {
                String content = httpGet(search_url);
                reader = new FundReader(content);

                msg.obj = reader.GetStringList();
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_SEARCH_DATA:
                    if (msg.obj != null) {
                        ArrayList<String> list = (ArrayList<String>) msg.obj;
                        autoCompleteAdapter = new AutoCompleteAdapter(MainActivity.this, list, -1);
                        acTextView.setAdapter(autoCompleteAdapter);
                    }
                    progressDialog.dismiss();
                    break;
                case GET_FUNDER_DETAIL:
                    if (msg.obj != null) {
                        FunderDetail detail = (FunderDetail) msg.obj;
                        tvDate.setText(detail.Date);
                        tvEstimateDate.setText(detail.EstimateDate);
                        tvEstimateRiseRate.setText(detail.EstimateRiseRate);
                        tvEstimateWorth.setText(detail.EstimateWorth);
                        tvUnitWorth.setText(detail.UnitWorth);
                        detailRelativeLayout.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(detail.Image);
                    }

                    inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken()
                            , InputMethodManager.HIDE_NOT_ALWAYS);

                    progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void updateDetail() {
        progressDialog = ProgressDialog.show(MainActivity.this, "正在获取最新信息...", "请稍候...", true, false);
        String code = reader.Search(acTextView.getText().toString());
        detailRelativeLayout.setVisibility(View.INVISIBLE);
        final String detailUrl = String.format(detail_url, code);
        final String imgUrl = String.format(img_url, code);
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = GET_FUNDER_DETAIL;

                try {
                    String content = httpGet(detailUrl);
                    Bitmap img = httpGetImage(imgUrl);

                    msg.obj = reader.GetDetail(content, img);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            updateDetail();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
