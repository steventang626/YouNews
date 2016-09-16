package com.ihandy.a2014011328.bigproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class Details extends AppCompatActivity {

    private WebView webView;
    private String weburl;
    private String title;
    private String source;
    private boolean isLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        source = intent.getStringExtra("source");
        isLiked = intent.getBooleanExtra("isLiked", false);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 根据传入的参数再去加载新的网页
                view.loadUrl(url);
                // 表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
                return true;
            }
        });
        webView.loadUrl(source);
        //webView.loadUrl("http://www.indiatimes.com/health/healthyliving/things-you-should-and-shouldn-t-say-to-someone-struggling-with-an-psychiatric-disorder-258915.html");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(isLiked == false){
            menu.findItem(R.id.like).setIcon(R.drawable.grey_favorite);}
        if(isLiked == true){
            menu.findItem(R.id.like).setIcon(R.drawable.red_heart);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.like) {
            isLiked = !isLiked;
            if(isLiked == false){
                item.setIcon(R.drawable.grey_favorite);
            Toast.makeText(this,"You don't like this news.",Toast.LENGTH_SHORT).show();}
            if(isLiked == true){
                item.setIcon(R.drawable.red_heart);
                Toast.makeText(this,"You like this news.",Toast.LENGTH_SHORT).show();
            }

            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            int pp = 0;
            if(isLiked) pp=1;
            else pp=0;
            values.put("like", pp);
            db.update("News", values, "title = ?",
                    new String[] {title});

            return true;
        }

        if (id == R.id.share) {
            Toast.makeText(this,"You clicked the 'share' button.",Toast.LENGTH_SHORT).show();
            shareMsg("Details","Share this news",title+"\nRead this news here : " + source, null);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if(!f.exists()) Log.i("hh", "kkkkkkkkk");
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("likeData", isLiked);
        setResult(RESULT_OK, intent);
        //Toast.makeText(this,"It's here",Toast.LENGTH_SHORT).show();
        finish();
    }

}
