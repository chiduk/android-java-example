package kr.co.toyappfactory.gosung.terms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import kr.co.toyappfactory.gosung.R;

public class PrivacyPolicy extends AppCompatActivity {

    private String privayPolicy= "브랜드 리서치 개인정보 수집·이용 동의\n" +
            "\n" +
            "회사는 회원님께 서비스의 원활한 제공을 위해 최소한의 범위 내에서 아래와 같이 개인정보 수집·이용합니다.\n" +
            "\n" +
            "(1) 회원 정보의 수집·이용목적, 수집항목, 보유·이용기간은 아래와 같습니다. \n" +
            "\t구분: 입력정보 \n" +
            "\t수집·이용목적: 회원제 서비스 이용 및 상담 관리\n" +
            "\t수집 항목: 전자우편주소, 비밀번호, 생년월일, 성별\n" +
            "\t보유·이용기간: 목적달성(회원탈퇴 등) 후 지체 없이 파기(단, 관련법령 및 회사정책에 따라 별도 보관되는 정보는 예외)\n" +
            "\n" +
            "(2) 법령에 의하여 수집·이용되는 이용자의 정보는 아래와 같은 수집목적으로 보관합니다.\n" +
            "\t법령/내부정책: 통신비밀보호법\n" +
            "\t수집·이용목적: 통신사실확인자료 제공\n" +
            "\t수집 항목: 로그기록, 접속지 정보 등\n" +
            "\t보유·이용기간: 3개월\n" +
            "\n" +
            "(3) 그 밖의 사항은 브랜드 리서치 개인정보취급방침 운영에 따릅니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String title = getResources().getString(R.string.privacy_policy);
        getSupportActionBar().setTitle(title);

        TextView textView = (TextView)findViewById(R.id.textview_privacy_policy);
        textView.setText(privayPolicy);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);

                finish();
                //this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_enter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
