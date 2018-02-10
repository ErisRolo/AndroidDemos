package com.example.guohouxiao.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guohouxiao on 2017/4/30.
 * 网络连接专用类
 */
public class FlickrFetchr {

    private GalleryItem mGalleryItem;

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "5acf271e7caaf7748cdc48939d14b107";

    //添加URL常量
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    //从指定URL获取原始数据并返回一个字节流数组
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        //根据传入的字符串参数创建一个URL对象
        URL url = new URL(urlSpec);
        //openConnection()方法用来创建一个指向要访问URL的连接对象，注意默认返回的是URLConnection，要强转成HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();//此时真正连接到指定的URL地址

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];//1KB
            //循环调用read()方法读取网络数据
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);//将读取的数据写入ByteArrayOutputStream字节数组中
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }


    //将getUrlBytes()方法返回的结果转换为String
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem.PhotosBean.PhotoBean> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem.PhotosBean.PhotoBean> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

/*    //传入整形参数作为页数，实现分页
    public List<GalleryItem.PhotosBean.PhotoBean> fetchItems(Integer page) {*/
    private List<GalleryItem.PhotosBean.PhotoBean> downloadGalleryItems(String url) {
        List<GalleryItem.PhotosBean.PhotoBean> items = new ArrayList<>();

        try {
/*            //用Uri.Builder构建完整的Flickr API请求URL
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    //Uri.Builder.appendQueryParameter()可自动转义查询字符串
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page",page.toString())//增加页数获取
                    .build().toString();*/
            String jsonString = getUrlString(url);//用重新构建的url获取JSON数据
            Log.i(TAG, "Received JSON: " + jsonString);
/*            JSONObject jsonBody = new JSONObject(jsonString);//将JSON数据解析进相应的java对象
            parseItems(items, jsonBody);//取出每张图片的信息，生成一个个GalleryItem对象，并添加到List中*/
            //GSON解析
            Gson gson = new Gson();
            mGalleryItem = gson.fromJson(jsonString,GalleryItem.class);
            items = mGalleryItem.getPhotos().getPhoto();
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } /*catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }*/

        return items;
    }

    //添加创建URL的辅助方法
    private String buildUrl(String method, String query) {
        Uri.Builder uriBulider = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD)) {
            uriBulider.appendQueryParameter("text",query);
        }

        return uriBulider.build().toString();
    }

/*    //JSON解析Flickr图片
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            //不是每张图片都有对应的url_s链接，所以需要添加一个检查
            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }*/

}
