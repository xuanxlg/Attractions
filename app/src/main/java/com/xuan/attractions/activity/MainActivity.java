package com.xuan.attractions.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xuan.attractions.R;
import com.xuan.attractions.http.HttpParameters;
import com.xuan.attractions.http.WikiServer;
import com.xuan.attractions.object.WikiContent;

public class MainActivity extends AppCompatActivity  implements HttpParameters {

    Button bt;
    TextView tv;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (Button)(findViewById(R.id.btCallWiki));
        tv = (TextView) (findViewById(R.id.tv));

        bt.setOnClickListener(btnListener);

    }

    private Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        final WikiContent wikiContent = new WikiServer().getExtract(TEST_ATTRACTION);
                        System.out.println("wikiContent.getContent(): "+wikiContent.getContent());
                        handler.post(new Runnable() {
                            public void run() {
                                tv.setText(wikiContent.getTitle()+"\n\n"+wikiContent.getContent());
                            }
                        });
                    } catch (final Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                tv.setText(e.toString());
                            }
                        });
                    }
                }
            }).start();

        }
    };
}
