package com.ihandy.a2014011328.bigproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

public class Favorites extends AppCompatActivity {

    public static final String ARGS_PAGE = "args_page";
    private static final int RESULT_OK = -1;
    private Handler handler = new Handler();
    private ArrayList<News> newses = new ArrayList<>();
    private ListView listView;
    private boolean save;
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView00);
        final NewsListAdapter Badapter = new NewsListAdapter();
        listView.setAdapter(Badapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                news = newses.get(position);

                if (news.getSource() == "null") {
                    Toast.makeText(Favorites.this, "Sorry, no more details.",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(Favorites.this, "You clicked:\n" + news.getTitle(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Favorites.this, Details.class);
                    //传出的参数有：title, url, isLiked

                    intent.putExtra("title", news.getTitle());
                    intent.putExtra("source", news.getSource());
                    intent.putExtra("isLiked", true);
                    startActivityForResult(intent, 2);

                }

            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();  //获取context的方法：this.getActivity()
        ImageLoader.getInstance().init(config);

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor = db.query("News", null, "like = ?", new String[]{String.valueOf(1)}, null, null, null);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 2:
                if(resultCode == RESULT_OK){
                    finish();
                    Intent intent0 = new Intent(Favorites.this, Favorites.class);
                    startActivity(intent0);
                }
                break;
            default:
        }
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
                convertView = getLayoutInflater().inflate(R.layout.news_item, parent, false);

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
