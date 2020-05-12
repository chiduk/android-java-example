package kr.co.toyappfactory.gosung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.PostResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Review extends AppCompatActivity {
    private static int REQUEST_CAMERA = 1;
    private static int SELECT_FILE = 2;
    private Uri fileUri;
    private Bitmap imageViewBitmap;

    private ImageAdapter imageAdapter;
    //private ImageView brandImageView1;
    private String selectedCategory;
    private EditText titleEditText;
    private ArrayList<ImageData> imageList;
    private EditText reviewEditText;

    int imageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        imageIndex = 1;

        imageList = new ArrayList<ImageData>();

        imageAdapter = new ImageAdapter();


        reviewEditText = (EditText)findViewById(R.id.edittext_review);

        titleEditText = (EditText)findViewById(R.id.edittext_title);


        addUploadButton();
        addSpinner();
        addDeletePicButton();
        addPicButton();
    }

    private void addSpinner(){
        Spinner categorySpinner = (Spinner)findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.brand_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

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
                    upload();
                }
            });
        }
    }

    private void addDeletePicButton(){
        final Button deletePicButton = (Button)findViewById(R.id.button_image_delete);
        deletePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.remove(0);

                Button addPicButton = (Button)findViewById(R.id.button_image_upload);
                addPicButton.setVisibility(View.VISIBLE);

                deletePicButton.setVisibility(View.GONE);

                ImageView uploadPicImageView = (ImageView)findViewById(R.id.imageview_pic);
                uploadPicImageView.setVisibility(View.GONE);
            }
        });
    }

    private void addPicButton(){
        Button button = (Button)findViewById(R.id.button_image_upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Review.this);
                builder.setTitle(R.string.select_pic)
                        .setItems(R.array.picSelection, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i){
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/image" + imageIndex + ".jpg");
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                        startActivityForResult(intent, REQUEST_CAMERA);
                                        break;

                                    case 1:
                                        Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent2.setType("image/*");
                                        intent2.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                                        startActivityForResult(intent2, SELECT_FILE);

                                        break;
                                }
                            }
                        });

                builder.create().show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.cancel:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage(R.string.message_cancel_writing).setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                AlertDialog alert = alertDialog.create();
                alert.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.message_cancel_writing).setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        // Do something

        // super.onBackPressed();

    }


    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if(resCode == Activity.RESULT_OK){
            if(reqCode == REQUEST_CAMERA ){

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/image" + imageIndex + ".jpg");
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

                ImageView uploadPic = (ImageView)findViewById(R.id.imageview_pic);
                uploadPic.setVisibility(View.VISIBLE);
                uploadPic.setImageBitmap(imageViewBitmap);

                Button uploadPicButton = (Button)findViewById(R.id.button_image_upload);
                Button deletePicButton = (Button)findViewById(R.id.button_image_delete);

                uploadPicButton.setVisibility(View.GONE);
                deletePicButton.setVisibility(View.VISIBLE);

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

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/image" + imageIndex + ".jpg");
                FileOutputStream outputStream = null;

                try{
                    outputStream = new FileOutputStream(file);
                    imageViewBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if( outputStream != null){
                        try {
                            outputStream.close();

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                ImageData imageData = new ImageData(imageViewBitmap, file.getAbsolutePath());

                imageList.add(imageData);
                imageAdapter.notifyDataSetChanged();

                ImageView uploadPic = (ImageView)findViewById(R.id.imageview_pic);
                uploadPic.setVisibility(View.VISIBLE);
                uploadPic.setImageBitmap(imageViewBitmap);

                Button uploadPicButton = (Button)findViewById(R.id.button_image_upload);
                Button deletePicButton = (Button)findViewById(R.id.button_image_delete);

                uploadPicButton.setVisibility(View.GONE);
                deletePicButton.setVisibility(View.VISIBLE);
            }

        }

        imageIndex++;
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

    private void upload(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);
        MediaType MEDIA_TYPE = MediaType.parse("image/png");
        //MediaType MEDIA_TYPE = MediaType.parse("multipart/form-data");
        HashMap<String, RequestBody> map = new HashMap<>(imageList.size());

        int i = 1;

        for( ImageData data : imageList){
            File file = new File(data.filePath);
            //String savePath = Environment.getExternalStorageDirectory() + File.separator + "mobydick"+String.valueOf(i)+".jpg";

            //Util.saveImage(BitmapFactory.decodeFile(file.getAbsolutePath() ), savePath);

            RequestBody reqFile = RequestBody.create(MEDIA_TYPE, file);
            map.put( "file\"; filename=\""+"image"+String.valueOf(i)+".png" , reqFile);
            i++;
        }


        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), Constants.getInstance().getUserId());
        RequestBody uniqueId = RequestBody.create(MediaType.parse("multipart/form-data"), JoinUserInfo.getInstance().getUniqueId());
        RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), selectedCategory);
        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), titleEditText.getText().toString());
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), JoinUserInfo.getInstance().getName());
        RequestBody review = RequestBody.create(MediaType.parse("multipart/form-data"), reviewEditText.getText().toString());

        ArrayList<int[]> hashTagSpans = HashTag.getSpans(reviewEditText.getText().toString(), '#');
        ArrayList<String> hashTagsArray = new ArrayList<>();
        for(int[] span : hashTagSpans){
            int start = span[0];
            int end = span[1];
            hashTagsArray.add(reviewEditText.getText().toString().substring(start, end));
        }

        Call<ResponseBody> call = apiService.uploadReview(uniqueId, userId, category, title, name, hashTagsArray, hashTagsArray.size(), review, map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {

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
