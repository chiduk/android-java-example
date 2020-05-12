package kr.co.toyappfactory.gosung;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.PostResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.Util;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModifyPost extends AppCompatActivity {

    private static int REQUEST_CAMERA = 1;
    private static int SELECT_FILE = 2;
    private Uri fileUri;
    private Bitmap imageViewBitmap;
    private ListView imageListView;
    private ImageAdapter imageAdapter;
    //private ImageView brandImageView1;

    private SimpleDraweeView imageview;

    private ArrayList<ImageData> imageList;
    private EditText postEditText;

    private String feedId;
    private String post;
    private ArrayList<String> imageSrcList;


    private int imageIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        feedId = intent.getStringExtra("feedId");
        post = intent.getStringExtra("post");
        imageSrcList = intent.getStringArrayListExtra("images");



        imageview = (SimpleDraweeView)findViewById(R.id.imageview_pic);

        if(imageSrcList.size() > 0){
            imageview.setImageURI(Constants.appServerHost + "/ul/ps/" + imageSrcList.get(0));
        }

        postEditText = (EditText)findViewById(R.id.edittext_post);
        postEditText.setText(post);

        imageIndex = 0;



        addUploadButton();
    }

    private void addUploadButton(){
        Button uploadButton = (Button)findViewById(R.id.button_upload);

        if(uploadButton != null){
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update();
                }
            });
        }
    }




    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if(resCode == Activity.RESULT_OK){
            if(reqCode == REQUEST_CAMERA ){

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(imageIndex)+".jpg");
                fileUri = Uri.parse(file.toString());
                imageViewBitmap = decodeSampledBitmapFromFile(file.toString(), 512, 384);

                try{
                    ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    int degree = 0;
                    switch (orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            System.out.println("90 degree");
                            degree = 90;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            System.out.println("180 degree");
                            degree = 180;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            System.out.println("270 degree");
                            degree = 270;
                            break;

                    }


                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);

                    imageViewBitmap = Bitmap.createBitmap(imageViewBitmap, 0, 0, imageViewBitmap.getWidth(), imageViewBitmap.getHeight(), matrix, true);

                    //brandImageView1.setImageBitmap(imageViewBitmap);

                    FileOutputStream outputStream = null;

                    try{
                        outputStream = new FileOutputStream(file);
                        imageViewBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if( outputStream != null){
                            outputStream.close();
                        }
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }

                ImageData imageData = new ImageData(imageViewBitmap, file.toString());

                imageList.add(imageData);
                imageAdapter.notifyDataSetChanged();

                imageIndex++;


            }else if(reqCode == SELECT_FILE){
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                imageViewBitmap = decodeSampledBitmapFromFile(filePath, 512, 384);
                //imageViewBitmap = Util.decodeFile(new File(filePath));
                int degree = 0;

                try {
                    ExifInterface exifInterface = new ExifInterface(filePath);
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            System.out.println("90 degree");
                            degree = 90;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            System.out.println("180 degree");
                            degree = 180;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            System.out.println("270 degree");
                            degree = 270;
                            break;

                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

                Matrix matrix = new Matrix();
                matrix.postRotate(degree);

                imageViewBitmap = Bitmap.createBitmap(imageViewBitmap, 0, 0, imageViewBitmap.getWidth(), imageViewBitmap.getHeight(), matrix, true);
                Util.saveImage(imageViewBitmap, Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(imageIndex)+".jpg");
                ImageData imageData = new ImageData(imageViewBitmap, Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(imageIndex)+".jpg");

                imageList.add(imageData);
                imageAdapter.notifyDataSetChanged();

                imageIndex++;
            }

        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null){
            imageList = savedInstanceState.getParcelableArrayList("imageData");
            imageAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("imageData", imageList);
    }


    @Override
    public void onResume(){
        super.onResume();

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if( height > reqHeight || width > reqWidth ){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    private void update(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);
        MediaType MEDIA_TYPE = MediaType.parse("image/png");
       /* HashMap<String, RequestBody> map = new HashMap<>(imageList.size());

        int i = 1;

        for( ImageData data : imageList){
            File file = new File(data.filePath);
            String savePath = Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(imageIndex)+".jpg";

            //Util.saveImage(BitmapFactory.decodeFile(file.getAbsolutePath() ), savePath);

            RequestBody reqFile = RequestBody.create(MEDIA_TYPE, file);
            map.put( "file\"; filename=\""+"image"+String.valueOf(i)+".png" , reqFile);
            i++;
        }*/


       // RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), Constants.getInstance().getUserId());
       // RequestBody fId = RequestBody.create(MediaType.parse("multipart/form-data"), feedId);
       // RequestBody post = RequestBody.create(MediaType.parse("multipart/form-data"), postEditText.getText().toString());

        String userId = Constants.getInstance().getUserId();
        String post = postEditText.getText().toString();


        ArrayList<int[]> hashTagSpans = HashTag.getSpans(postEditText.getText().toString(), '#');

        ArrayList<String> hashTagsArray = new ArrayList<>();
        for(int[] span : hashTagSpans){
            int start = span[0];
            int end = span[1];
            hashTagsArray.add(postEditText.getText().toString().substring(start, end));
        }

        Call<PostResponse> call = apiService.updatePost(userId, feedId, hashTagsArray, hashTagsArray.size(), post);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Bundle bundle = new Bundle();
                bundle.putString("post", postEditText.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ImageAdapter(){
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.album_image, null);

            ImageView image = (ImageView)view.findViewById(R.id.imageview_image);
            Button cancelButton = (Button)view.findViewById(R.id.button_cancel);

            image.setImageBitmap(imageList.get(position).bitmap);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageList.remove(position);
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }



    private class ImageLoader extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int index = 1;
            for(String src : imageSrcList){
                String url = Constants.appServerHost + "/ul/ss" + src;

                Bitmap bitmap = getBitmapFromURL(url);
                Util.saveImage(bitmap, Environment.getExternalStorageDirectory() + File.separator + "mobydick"+String.valueOf(imageIndex)+".jpg");
                ImageData imageData = new ImageData(bitmap,  Environment.getExternalStorageDirectory() + File.separator + "mobydick"+String.valueOf(imageIndex)+".jpg");
                imageList.add(imageData);
                imageIndex++;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            imageAdapter.notifyDataSetChanged();
        }


    }

    private class ImageData implements Parcelable {

        private Bitmap bitmap;
        private String filePath;

        public ImageData(Bitmap bitmap, String filePath){
            this.bitmap = bitmap;
            this.filePath = filePath;
        }

        public ImageData(Parcel in){
            super();
            readFromParcel(in);
        }

        public final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>(){

            @Override
            public ImageData createFromParcel(Parcel source) {
                return new ImageData(source);
            }

            @Override
            public ImageData[] newArray(int size) {
                return new ImageData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(bitmap, flags);
            dest.writeString(filePath);
        }

        public void readFromParcel(Parcel in){

            ClassLoader classLoader = Bitmap.class.getClassLoader();

            bitmap = in.readParcelable(classLoader);
            filePath = in.readString();
        }
    }
}
