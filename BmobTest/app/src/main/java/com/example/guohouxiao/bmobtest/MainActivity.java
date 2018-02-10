package com.example.guohouxiao.bmobtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "93c511d69a6a809036927cf188442b20 ");

        Button addBtn = (Button) findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注意：不能调用setObjectId("")方法
                Person person = new Person();
                person.setName("lucky");
                person.setAddress("北京海淀");
                person.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this,"添加数据成功，返回objectId为：" + objectId, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this,"创建数据失败",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });

        Button getBtn = (Button) findViewById(R.id.get_btn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<Person> bmobQuery = new BmobQuery<Person>();
                bmobQuery.getObject("9be59a550b", new QueryListener<Person>() {
                    @Override
                    public void done(Person object, BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "查询成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "查询失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        Button modifyBtn = (Button) findViewById(R.id.modify_btn);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                final Person person = new Person();
                person.setAddress("北京朝阳");
                person.update("b86c7fd89a", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "更新成功：" + person.getUpdatedAt(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "更新失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });*/

                final Person person = new Person();
                person.setValue("address", "北京朝阳");
                person.update("9be59a550b", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "更新成功：" + person.getUpdatedAt(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "更新失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        Button deleteBtn = (Button) findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Person person = new Person();
                person.setObjectId("b86c7fd89a");
                person.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "删除成功：" + person.getUpdatedAt(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "删除失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
