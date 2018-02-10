package com.example.guohouxiao.photogallery;

import android.net.Uri;

import java.util.List;

/**
 * Created by guohouxiao on 2017/5/2.
 * GSON解析的item数据，模型对象类
 */

public class GalleryItem {



    private PhotosBean photos;
    private String stat;

    public PhotosBean getPhotos() {
        return photos;
    }

    public void setPhotos(PhotosBean photos) {
        this.photos = photos;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public static class PhotosBean {


        private int page;
        private int pages;
        private int perpage;
        private int total;
        private List<PhotoBean> photo;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public void setPerpage(int perpage) {
            this.perpage = perpage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<PhotoBean> getPhoto() {
            return photo;
        }

        public void setPhoto(List<PhotoBean> photo) {
            this.photo = photo;
        }

        public static class PhotoBean {

            private String id;
            private String owner;
            private String secret;
            private String server;
            private int farm;
            private String title;
            private int ispublic;
            private int isfriend;
            private int isfamily;
            private String url_s;
            private String height_s;
            private String width_s;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getOwner() {
                return owner;
            }

            public void setOwner(String owner) {
                this.owner = owner;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public int getFarm() {
                return farm;
            }

            public void setFarm(int farm) {
                this.farm = farm;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getIspublic() {
                return ispublic;
            }

            public void setIspublic(int ispublic) {
                this.ispublic = ispublic;
            }

            public int getIsfriend() {
                return isfriend;
            }

            public void setIsfriend(int isfriend) {
                this.isfriend = isfriend;
            }

            public int getIsfamily() {
                return isfamily;
            }

            public void setIsfamily(int isfamily) {
                this.isfamily = isfamily;
            }

            public String getUrl_s() {
                return url_s;
            }

            public void setUrl_s(String url_s) {
                this.url_s = url_s;
            }

            public String getHeight_s() {
                return height_s;
            }

            public void setHeight_s(String height_s) {
                this.height_s = height_s;
            }

            public String getWidth_s() {
                return width_s;
            }

            public void setWidth_s(String width_s) {
                this.width_s = width_s;
            }

            public Uri getPhotoPageUri() {
                return Uri.parse("http://www.flickr.com/photos/")
                        .buildUpon()
                        .appendPath(owner)
                        .appendPath(id)
                        .build();
            }
        }
    }
}
