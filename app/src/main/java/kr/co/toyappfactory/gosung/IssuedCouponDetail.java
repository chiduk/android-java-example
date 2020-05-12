package kr.co.toyappfactory.gosung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

public class IssuedCouponDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issued_coupon_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        String serialNumber = intent.getStringExtra("serialCode");
        String couponId = intent.getStringExtra("couponId");
        String couponProvider = intent.getStringExtra("couponProvider");
        String couponName = intent.getStringExtra("couponName");
        String couponImage = intent.getStringExtra("couponImage");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");

        SimpleDraweeView couponIcon = (SimpleDraweeView)findViewById(R.id.imageview_coupon_icon);
        TextView couponProviderTextView = (TextView)findViewById(R.id.textview_coupon_provider);
        TextView couponNameTextView = (TextView)findViewById(R.id.textview_coupon_name);
        TextView couponDateTextView = (TextView)findViewById(R.id.textview_coupon_date);
        TextView couponSerialCode = (TextView)findViewById(R.id.textview_coupon_serial_number);

        couponIcon.setImageURI(couponImage);
        couponProviderTextView.setText(couponProvider);
        couponNameTextView.setText(couponName);
        couponDateTextView.setText(startDate.split("T")[0] + " ~ " + endDate.split("T")[0]);

        couponSerialCode.setText(serialNumber);
    }
}
