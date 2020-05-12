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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

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
import java.util.Arrays;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.PostResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.Util;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModifyReview extends AppCompatActivity {

    private static int REQUEST_CAMERA = 1;
    private static int SELECT_FILE = 2;
    private Uri fileUri;
    private Bitmap imageViewBitmap;
    private ListView imageListView;
    private ImageAdapter imageAdapter;
    //private ImageView brandImageView1;

    private ArrayList<ImageData> imageList;
    private EditText reviewEditText;
    private EditText titleEditText;

    private String feedId;
    private String review;
    private String title;
    private String category;
    private String selectedCategory;

    private ArrayList<String> imageSrcList;

    private int imageIndex;

    private SimpleDraweeView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_review);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        feedId = intent.getStringExtra("feedId");
        review = intent.getStringExtra("review");
        title = intent.getStringExtra("title");
        category = intent.getStringExtra("category");

        imageSrcList = intent.getStringArrayListExtra("images");

/*        imageList = new ArrayList<ImageData>();
        imageListView = (ListView)findViewById(R.id.listview_images);
        imageAdapter = new ImageAdapter();
        imageListView.setAdapter(imageAdapter);*/


        imageview = (SimpleDraweeView)findViewById(R.id.imageview_pic);
        if(imageSrcList.size() > 0){
            imageview.setImageURI(Constants.appServerHost + "/ul/ps/" + imageSrcList.get(0));
        }


        reviewEditText = (EditText)findViewById(R.id.edittext_review);
        reviewEditText.setText(review);

        titleEditText = (EditText)findViewById(R.id.edittext_title);
        titleEditText.setText(title);

        imageIndex = 0;


        addSpinner();
        addUploadButton();
    }

    private void addSpinner(){
        Spinner categorySpinner = (Spinner)findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.brand_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        int categoryIndex = Arrays.asList(R.array.brand_category).indexOf(category);
        categorySpinner.setSelection(categoryIndex);

        selectedCategory = category;

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        /*HashMap<String, RequestBody> map = new HashMap<>(imageList.size());

        int i = 1;

        for( ImageData data : imageList){
            File file = new File(data.filePath);
            //String savePath = Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(i)+".jpg";

            //Util.saveImage(BitmapFactory.decodeFile(file.getAbsolutePath() ), savePath);

            RequestBody reqFile = RequestBody.create(MEDIA_TYPE, file);
            map.put( "file\"; filename=\""+"image"+String.valueOf(i)+".png" , reqFile);
            i++;
        }*/


        //RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), Constants.getInstance().getUserId());
        //RequestBody fId = RequestBody.create(MediaType.parse("multipart/form-data"), feedId);
        //RequestBody reviewTitle = RequestBody.create(MediaType.parse("multipart/form-data"), titleEditText.getText().toString());
        //RequestBody reviewCategory = RequestBody.create(MediaType.parse("multipart/form-data"), category);
        //RequestBody rv = RequestBody.create(MediaType.parse("multipart/form-data"), reviewEditText.getText().toString());

        String userId = Constants.getInstance().getUserId();

        ArrayList<int[]> hashTagSpans = HashTag.getSpans(reviewEditText.getText().toString(), '#');
        ArrayList<String> hashTagsArray = new ArrayList<>();
        for(int[] span : hashTagSpans){
            int start = span[0];
            int end = span[1];
            hashTagsArray.add(reviewEditText.getText().toString().substring(start, end));
        }

        Call<ResponseBody> call = apiService.updateReview(userId, feedId, titleEditText.getText().toString(), category, hashTagsArray, hashTagsArray.size(), reviewEditText.getText().toString()/*, map*/);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent();
                intent.putExtra("title", titleEditText.getText().toString());
                intent.putExtra("category", selectedCategory);
                intent.putExtra("review", reviewEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
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

    private class ImageLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int index = 1;
            for(String src : imageSrcList){
                String url = Constants.appServerHost + "/ul/ps/" + src;

                Bitmap bitmap = getBitmapFromURL(url);
                Util.saveImage(bitmap, Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(imageIndex)+".jpg");
                ImageData imageData = new ImageData(bitmap,  Environment.getExternalStorageDirectory() + File.separator + "MobyDick/mobydick"+String.valueOf(imageIndex)+".jpg");
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
