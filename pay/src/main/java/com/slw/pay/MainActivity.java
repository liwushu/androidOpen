package com.slw.pay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String URL="https://openapi.alipay.com/gateway.do?version=1.0&biz_content=%7B%22biz_no%22%3A%22ZM201709083000000878700737183754%22%7D&app_id=2017082308346242&method=zhima.customer.certification.query&sign_type=RSA&timestamp=2017-09-08+16%3A45%3A53&charset=utf-8&return_url=https%3A%2F%2Fdev2.faceu.mobi%2Fv1%2Flive%2Fzmrz%2Fcallback&sign=pHLRMkmIfYvCSxnUFWyvD67S82YdmLrcpEnh3QXD0IFea1pR0zuBYgtvfemzGrhqh3OgkUnUzX2PMEGNchG9LLZrmvNZHWDf8asG7dDlkmIzYxvJi6zXUzozphcX1sQ3S1aQBU6eXaESaXKHpS0VF18KOBNrnyFRvU8czWkTzF4%3D";
    Button btnVerify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bindListener();
    }

    private void initViews() {
        btnVerify = (Button)findViewById(R.id.verify);
    }

    private void bindListener() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyManager.doVerify(MainActivity.this,URL);
            }
        });
    }
}
