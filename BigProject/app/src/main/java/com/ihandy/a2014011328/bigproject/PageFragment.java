package com.ihandy.a2014011328.bigproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PageFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";
    private static final int RESULT_OK = -1;
    private int mPage;

    private Handler handler = new Handler();
    private ArrayList<News> newses = new ArrayList<>();
    private ListView listView;
    private PullToRefreshListView listView1;
    private boolean save;
    private News news;
    private int pos;

//    ListView list;
//    String[] data = {"Apple","Banana","Cherry"};
//    List<News> newsList = new ArrayList<News>();

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();

        args.putInt(ARGS_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page,container,false);
        //TextView textView = (TextView) view.findViewById(R.id.textView);
        //textView.setText("第"+mPage+"页");
        listView1 = (PullToRefreshListView) view.findViewById(R.id.listView);
        //listView = (ListView) view.findViewById(R.id.listView);
        listView1.setMode(PullToRefreshBase.Mode.BOTH);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView1.setAdapter(Badapter);

        listView1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            // 下拉Pulling Down
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉的时候刷新新闻列表

                ConnectivityManager cwjManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()){

                newses.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String cat ="";
                        cat =  MyFragmentPagerAdapter.titles1[mPage-1];
                        HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category="+cat);
                        String body = request.body();
                        try {
                            JSONObject jsonObject = new JSONObject(body).getJSONObject("data"); //字符串转JSONObject, 但必须catch JSONException
                            JSONArray jsonArray = jsonObject.getJSONArray("news"); //获取Json格式的Image数组
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                                String title = jsonObject2.getString("title");
                                String origin = jsonObject2.getString("origin");
                                String category = jsonObject2.getString("category");
                                long id = jsonObject2.getLong("news_id");
                                JSONArray jsonArray2 = jsonObject2.getJSONArray("imgs");
                                JSONObject jsonObject3 = (JSONObject) jsonArray2.opt(0);
                                String image = jsonObject3.getString("url");
                                //Log.i("The url: ", image);
                                String src ;
                                src = jsonObject2.getString("source");
                                if(src == "null"){
                                    newses.add(new News(title, origin, image, id, category, "null"));
                                }
                                else{
                                    JSONObject jsonObject4 = jsonObject2.getJSONObject("source");
                                    src = jsonObject4.getString("url");
                                    newses.add(new News(title, origin, image, id, category, src));
                                }
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Badapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                    Toast.makeText(getActivity(),"Refreshing finished",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Sorry. Can not connect to the Internet,\nrefreshment failed.",Toast.LENGTH_LONG).show();
                }

//                mAdapter.notifyDataSetChanged();
                new FinishRefresh().execute();
            }

            // 上拉Pulling Up
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉的时候读取更多的新闻

                ConnectivityManager cwjManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        News last = newses.get(newses.size() - 1);
                        long lastId = last.getId();

                        //newses.clear();
                        lastId -- ;
                        String cat ="";
                        cat =  MyFragmentPagerAdapter.titles1[mPage-1];
                        HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category="+cat+"&max_news_id="+lastId);
                        String body = request.body();
                        try {
                            JSONObject jsonObject = new JSONObject(body).getJSONObject("data"); //字符串转JSONObject, 但必须catch JSONException
                            JSONArray jsonArray = jsonObject.getJSONArray("news"); //获取Json格式的Image数组
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                                String title = jsonObject2.getString("title");
                                String origin = jsonObject2.getString("origin");
                                String category = jsonObject2.getString("category");
                                long id = jsonObject2.getLong("news_id");
                                JSONArray jsonArray2 = jsonObject2.getJSONArray("imgs");
                                JSONObject jsonObject3 = (JSONObject) jsonArray2.opt(0);
                                String image = jsonObject3.getString("url");
                                //Log.i("The url: ", image);
                                String src ;
                                src = jsonObject2.getString("source");
                                if(src == "null"){
                                    newses.add(new News(title,origin,image,id,category,"null"));
                                }
                                else{
                                    JSONObject jsonObject4 = jsonObject2.getJSONObject("source");
                                    src = jsonObject4.getString("url");
                                    newses.add(new News(title, origin, image, id, category, src));}
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Badapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();}
                else{
                    Toast.makeText(getActivity(),"Can not connect to the Internet >< ",Toast.LENGTH_SHORT).show();
                }

//                mAdapter.notifyDataSetChanged();
                new FinishRefresh().execute();
            }

        });

        //view.postInvalidate();

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = newses.get(position - 1);

                pos = position;
                if(news.getSource() == "null"){
                    Toast.makeText(getActivity(), "Sorry, no more details.",
                            Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(getActivity(), "You clicked:\n"+ news.getTitle(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(),Details.class);
                    //传出的参数有：title, url, isLiked

                    intent.putExtra("title", news.getTitle());
                    intent.putExtra("source", news.getSource());
                    intent.putExtra("isLiked", news.getIsLiked());
                    startActivityForResult(intent, 11);

                }

            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.getActivity()).build();  //获取context的方法：this.getActivity()
        ImageLoader.getInstance().init(config);

        ConnectivityManager cwjManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()){

            //网络请求必须写在新起的线程中
            new Thread(new Runnable() {
            @Override
            public void run() {
                String cat ="";
                cat =  MyFragmentPagerAdapter.titles1[mPage-1];
//                switch(mPage)
//                {
//                    case 1: cat = "Entertainment";break;
//                    case 2: cat = "Health";break;
//                    case 3: cat = "National";break;
//                    case 4: cat = "Sports";break;
//                    case 5: cat = "Technology";break;
//                    case 6: cat = "Top_Stories";break;
//                    case 7: cat = "World";break;
//
//                }
                //HttpRequest request = HttpRequest.get("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=6&nc=1397809837851&pid=hp");//导入URL
                HttpRequest request = HttpRequest.get("http://assignment.crazz.cn/news/query?locale=en&category="+cat);

                        String body = request.body();
                Log.i("Print", body); //在Logcat中将get到的信息打印出来

                try {
                    JSONObject jsonObject = new JSONObject(body).getJSONObject("data"); //字符串转JSONObject, 但必须catch JSONException

                    /* －－－ JSON数据处理 －－－ */
                    //Log.i("Json", jsonObject.toString()); //这里打印出的信息，是格式化的规整可读的信息，注意与body（）的不同
                    Log.i("here","有执行到这里哦 "+ MyFragmentPagerAdapter.titles1[mPage-1]);

                    JSONArray jsonArray = jsonObject.getJSONArray("news"); //获取Json格式的Image数组
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                        String title = jsonObject2.getString("title");
                        String origin = jsonObject2.getString("origin");
                        String category = jsonObject2.getString("category");
                        long id = jsonObject2.getLong("news_id");
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("imgs");
                        JSONObject jsonObject3 = (JSONObject) jsonArray2.opt(0);
                        String image = jsonObject3.getString("url");
                        //Log.i("The url: ", image);
                        String src ;
                        src = jsonObject2.getString("source");
                        if(src == "null"){
                            //Log.i("The source : ","null");
                            newses.add(new News(title,origin,image,id,category,"null"));

                            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
                            Cursor cursor = db.query("News",null,"title = ?",new String[]{title},null,null,null);
                            if(!cursor.moveToFirst()){
                            ContentValues values = new ContentValues();
                            values.put("image", image);
                            values.put("title", title);
                            values.put("origin", origin);
                            values.put("source", "null");
                            values.put("id", id);
                            values.put("category", category);
                            values.put("like", 0);
                            db.insert("News", null, values);
                            values.clear();}
                            else{}

                        }
                        else{
                            JSONObject jsonObject4 = jsonObject2.getJSONObject("source");
                            src = jsonObject4.getString("url");
                            //Log.i("The source : ",src);
                        newses.add(new News(title, origin, image, id, category, src));

                            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
                            Cursor cursor = db.query("News",null,"title = ?",new String[]{title},null,null,null);
                            if(!cursor.moveToFirst()){
                                ContentValues values = new ContentValues();
                                values.put("image", image);
                                values.put("title", title);
                                values.put("origin", origin);
                                values.put("source", src);
                                values.put("id", id);
                                values.put("category", category);
                                values.put("like", 0);
                                db.insert("News", null, values);
                                values.clear();}
                            else{}
                        }
                        //newses.add(new News(jsonArray.getJSONObject(i)));
                    }

                    //非主线程无法修改UI，所以使用handler将修改UI的代码抛到主线程做
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Badapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        }
        else
        {
            Toast.makeText(getActivity(),"Can not connect to the Internet >< ",Toast.LENGTH_SHORT).show();

            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
            Cursor cursor = db.query("News", null, "category = ?", new String[]{MyFragmentPagerAdapter.titles00[mPage-1]}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor
                            .getColumnIndex("title"));
                    String origin = cursor.getString(cursor
                            .getColumnIndex("origin"));
                    String category = cursor.getString(cursor
                            .getColumnIndex("category"));
                    String src = cursor.getString(cursor
                            .getColumnIndex("source"));
                    String image = cursor.getString(cursor
                            .getColumnIndex("image"));
                    long id = (long)cursor.getInt(cursor
                            .getColumnIndex("id"));
                    newses.add(new News(title, origin, image, id, category, src));
                } while (cursor.moveToNext());
            }
            cursor.close();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Badapter.notifyDataSetChanged();
                }
            });
        }

        return view;
    }
//  The end of onCreateView.

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 11:
                if(resultCode == RESULT_OK){
                    save = data.getBooleanExtra("likeData",false);
                    Log.d("Here",save+"");
                    news.setIsLiked(save);
                }
                break;
            default:
        }
    }

    public void initNews(){
//        News a1 = new News("As GST looms, many Indian companies find themselves unprepared","indiatoday",R.drawable.news1);
//        newsList.add(a1);
//        News a2 = new News("As GST looms, many Indian companies find themselves unprepared","indiatoday",R.drawable.news1);
//        newsList.add(a2);
//        News a3 = new News("As GST looms, many Indian companies find themselves unprepared","indiatoday",R.drawable.news1);
//        newsList.add(a3);
//        News a4 = new News("As GST looms, many Indian companies find themselves unprepared","indiatoday",R.drawable.news1);
//        newsList.add(a4);
    }

    public DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型//
                .build();//构建完成
        return options;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView copyright;
        TextView source;
    }

    // 上拉下拉所用
    private class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            listView1.onRefreshComplete();
        }
    }

    public class NewsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newses.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                //convertView = getLayoutInflater().inflate(R.layout.image_list_item, parent, false);
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.news_item, parent, false);

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.news_image);
                viewHolder.copyright = (TextView) convertView.findViewById(R.id.news_name);
                viewHolder.source = (TextView) convertView.findViewById(R.id.news_source);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //异步加载图片方法
            ImageLoader.getInstance().displayImage(newses.get(position).getImage(), viewHolder.imageView, getDisplayOption());
            viewHolder.copyright.setText(newses.get(position).getTitle());
            viewHolder.source.setText(newses.get(position).getOrigin());

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
    }

}

class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public int COUNT = 7;
    public int num;
    public static String[] titles00 = new String[]{"Entertainment", "health", "National", "Sports","technology","Top Stories","world"};
    public static String[] titles03 = new String[]{"Entertainment", "health", "National", "Sports","technology","Top Stories","world"};// For database
    public static String[] titles01 = new String[]{"Entertainment", "Health", "National", "Sports","Technology","Top Stories","World"};
    public static String[] titles02 = new String[]{"Entertainment", "Health", "National", "Sports","Technology","Top_Stories","World"};
    public static String[] titles  = new String[]{"Entertainment", "Health", "National", "Sports","Technology","Top Stories","World"};   // For users
    public static String[] titles1 = new String[]{"Entertainment", "Health", "National", "Sports","Technology","Top_Stories","World"};  // For developers
//    public int COUNT = 7;
//    private String[] titles = new String[]{"Entertainment", "Health", "National", "Sports","Technology","Top Stories","World"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean is[] = new boolean[7];
        num = 0;
        for(int i =1;i<8;i++){
            is[i-1] = settings.getBoolean("s"+i,true);
            if(is[i-1] == true) {
                num++;
                titles[num-1] = titles01[i-1];
                titles1[num-1] = titles02[i-1];
                titles00[num-1] = titles03[i-1];
            }
            Log.i("here","The settings of "+i+" is "+is[i-1]);
        }
        COUNT = num;

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

