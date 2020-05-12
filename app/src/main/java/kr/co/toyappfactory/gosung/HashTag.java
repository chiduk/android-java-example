package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.toyappfactory.gosung.response.HashTagSearchResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chiduk on 2016. 9. 14..
 */
public class HashTag extends ClickableSpan {

    private Context context;
    private TextPaint textPaint;

    public HashTag(Context context){
        super();
        this.context = context;
    }

    @Override
    public void updateDrawState(TextPaint ds){
        textPaint = ds;
        ds.setColor(ds.linkColor);
        //ds.setARGB(255, 30, 144, 255);
    }

    @Override
    public void onClick(View widget) {
        TextView textView = (TextView)widget;
        Spanned spanned = (Spanned)textView.getText();
        int start = spanned.getSpanStart(this);
        int end = spanned.getSpanEnd(this);
        String theWord = spanned.subSequence(start, end).toString();

        //Toast.makeText(context, String.format("Tags for tags %s", theWord), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, SearchResult.class);
        intent.putExtra("hashTag", theWord);
        context.startActivity(intent);

        Constants constants = Constants.getInstance();
        //String filename = fileUri.toString();




    }

    public static ArrayList<int[]> getSpans(String body, char prefix){
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher  = pattern.matcher(body);

        while(matcher.find()){
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return spans;
    }
}
