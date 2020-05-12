package kr.co.toyappfactory.gosung.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kr.co.toyappfactory.gosung.response.UploadSSResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chiduk on 2016. 6. 21..
 */
public class Util {
    public static void takeScreenshot(View v1, String path) {



        try {
            // image naming and path  to include sd card  appending name you choose for file
            //String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "sshot" + ".jpg";

            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(path);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    public static void shareRank(final Context context, View view){
        Util.takeScreenshot(view, Environment.getExternalStorageDirectory().toString() + "/" + "sshot" + ".jpg");
        final String stringText = "브랜드왕";
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "sshot" + ".jpg";

        saveImage(reduceBitmap(mPath, 540,960), mPath);


        File file = new File(mPath);



        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), Constants.getInstance().getUserId());


        Call<UploadSSResponse> call = apiService.uploadSS(userId, body);
        call.enqueue(new Callback<UploadSSResponse>() {
            @Override
            public void onResponse(Call<UploadSSResponse> call, Response<UploadSSResponse> response) {
                try {

                    KakaoLink kakaoLink = KakaoLink.getKakaoLink(context);
                    KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

                    String filename = response.body().filename;
                    System.out.println(response.body().filename);
                    kakaoTalkLinkMessageBuilder.addText(Constants.appServerHost + "/ul/ss/" + filename);
                    kakaoTalkLinkMessageBuilder.addImage(Constants.appServerHost + "/ul/ss/" + filename, 1080, 1920);
                    //kakaoTalkLinkMessageBuilder.addImage("https://unsplash.it/1080/1920", 1080, 1920);
                    //kakaoTalkLinkMessageBuilder.addWebLink(Constants.appServerHost + ":" + Constants.appServerPort + "/ul/" + filename);
                    //kakaoTalkLinkMessageBuilder.addWebLink("https://unsplash.it/1080/1920?random");

                    kakaoTalkLinkMessageBuilder.addAppButton("모비딕 열기", new AppActionBuilder().build());
                                                    /*.setAndroidExecuteURLParam("target=main")
                                                    .setIOSExecuteURLParam("target=main", AppActionBuilder.DEVICE_TYPE.PHONE).build());
*/
                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, context);
                } catch (com.kakao.util.KakaoParameterException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UploadSSResponse> call, Throwable t) {
                System.out.println("Screen shot upload fail " + t.getMessage());
            }
        });
    }

    public static Bitmap reduceBitmap(String file , int width, int height){
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    public static void saveImage(Bitmap bitmap, String filePath){
        File fileCacheItem = new File(filePath);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap decodeFile(File f){
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}
