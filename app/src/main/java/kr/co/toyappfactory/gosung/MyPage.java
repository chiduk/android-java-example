package kr.co.toyappfactory.gosung;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.CouponResponse;
import kr.co.toyappfactory.gosung.response.IssuedCouponResponse;
import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.response.RefreshPostResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPage extends AppCompatActivity {
    private static int REQUEST_CAMERA = 4;
    private static int SELECT_FILE = 5;
    private static int REFRESH_VIEW = 1;
    private SimpleDraweeView profile;
    private Bitmap imageViewBitmap;
    private TextView emailTextView;
    private TextView nameTextView;
    private TextView brandStarTextView;
    private RecyclerView likedBrandsRecyclerView;
    private RecyclerView issuedCouponRecycleView;
    private ArrayList<LikeAndDislikeCount> likedBrandList;
    private ArrayList<IssuedCouponResponse> currentCouponList;
    private LikedBrandsHorizontalAdapter likedBrandsHorizontalAdapter;
    private IssuedCouponHorizontalAdapter issuedCouponHorizontalAdapter;
    private ArrayList<RefreshPostResponse> postList;
    private ListView postListView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        likedBrandList = new ArrayList<>();
        currentCouponList = new ArrayList<>();

        likedBrandsRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_horizontal_liked_brands);

        LinearLayoutManager likedBrandsHorizontalLLM = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        likedBrandsRecyclerView.setLayoutManager(likedBrandsHorizontalLLM);

        likedBrandsHorizontalAdapter = new LikedBrandsHorizontalAdapter();
        likedBrandsRecyclerView.setAdapter(likedBrandsHorizontalAdapter);

        issuedCouponRecycleView = (RecyclerView)findViewById(R.id.recyclerview_horizontal_issued_coupon);

        LinearLayoutManager issuedCouponHorizontalLLM = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        issuedCouponRecycleView.setLayoutManager(issuedCouponHorizontalLLM);
        issuedCouponHorizontalAdapter = new IssuedCouponHorizontalAdapter();
        issuedCouponRecycleView.setAdapter(issuedCouponHorizontalAdapter);

        postList = new ArrayList<>();
        postListView = (ListView)findViewById(R.id.listview_my_feed);
        postAdapter = new PostAdapter();
        postListView.setAdapter(postAdapter);

        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyPage.this, PostSingleView.class);
                intent.putExtra("feedId", postList.get(i)._id);
                intent.putExtra("date", postList.get(i).date);
                intent.putExtra("uniqueId", postList.get(i).uniqueId);
                intent.putExtra("userId", postList.get(i).userId);
                intent.putExtra("name", postList.get(i).name);
                intent.putExtra("post", postList.get(i).post);
                intent.putStringArrayListExtra("images", postList.get(i).images);
                intent.putExtra("likeCount", postList.get(i).like);
                intent.putExtra("createMenu", true);
                startActivityForResult(intent, REFRESH_VIEW);
            }
        });


        addProfile();
        addEmail();
        addName();
        addBrandStar();

        addMyLogInInfoButton();
        addMorePostButton();
        addMoreCouponButton();

        getLikedBrands();
        getCurrentCoupon();
        getPost();

        profile.setImageURI(Constants.appServerHost + "/ul/pf/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");

    }


    private void getPost(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<RefreshPostResponse>> call = apiService.getMyPost(params);
        call.enqueue(new Callback<ArrayList<RefreshPostResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RefreshPostResponse>> call, Response<ArrayList<RefreshPostResponse>> response) {
                if(response.body() != null){

                    postList = response.body();
                    postAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<RefreshPostResponse>> call, Throwable t) {

            }
        });
    }

    private class PostAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public PostAdapter(){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return postList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = inflater.inflate(R.layout.post_row, null);

            SimpleDraweeView iconImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_icon);
            TextView header1 = (TextView)rowView.findViewById(R.id.textview_header1);
            TextView header2 = (TextView)rowView.findViewById(R.id.textview_header2);
            TextView header3 = (TextView)rowView.findViewById(R.id.textview_header3);

            iconImageView.setImageURI(Constants.appServerHost + "/ul/pf/" + postList.get(i).uniqueId + ".jpg");
            header1.setText(postList.get(i).name);
            header2.setText(postList.get(i).post);
            header3.setText(postList.get(i).date.split("T")[0]);

            SimpleDraweeView post1ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_1);
            SimpleDraweeView post2ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_2);

            if(postList.get(i).images.size() > 0){

                String url = Constants.appServerHost +"/ul/ps/" + postList.get(i).images.get(0);
                post2ImageView.setImageURI(url);
            }

/*

            if(postList.get(i).images.size() > 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/" + postList.get(i).images.get(0));
                post2ImageView.setImageURI(Constants.appServerHost +"/ul/" + postList.get(i).images.get(1));
            } else if( postList.get(i).images.size() == 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/" + postList.get(i).images.get(0));
            }
*/
            return rowView;
        }
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

    private void getLikedBrands() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<LikeAndDislikeCount>> call = apiService.getLikedBrands(params);
        call.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {
                ArrayList<String> list = new ArrayList<String>();
                Constants constants = Constants.getInstance();

                if(response.body() != null){
                    likedBrandList = response.body();
                    likedBrandsHorizontalAdapter.notifyDataSetChanged();
                }

                for (LikeAndDislikeCount elem : response.body()) {
                    String brandId = elem.brandId;
                    //Todo: Show favorite brands per category
                    String url = Constants.appServerHost + "/big/b/" + brandId + "/thm.jpeg";
                    list.add(url);
                }

                //gridView.setAdapter(new FavBrandImageAdapter(context, list));

            }

            @Override
            public void onFailure(Call<ArrayList<LikeAndDislikeCount>> call, Throwable t) {

            }
        });
    }

    private void getCurrentCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<IssuedCouponResponse>> call = apiService.getCurrentCoupon(params);
        call.enqueue(new Callback<ArrayList<IssuedCouponResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<IssuedCouponResponse>> call, Response<ArrayList<IssuedCouponResponse>> response) {
                currentCouponList = response.body();
                issuedCouponHorizontalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<IssuedCouponResponse>> call, Throwable t) {

            }
        });
    }

    private void addMyLogInInfoButton(){
        ImageButton button = (ImageButton)findViewById(R.id.imagebutton_modify_my_info);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyPage.this, MyLogInInfo.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void addMorePostButton(){
        ImageButton morePostButton = (ImageButton)findViewById(R.id.button_more_post);
        morePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPage.this, MyPost.class);
                startActivity(intent);
            }
        });
    }

    private void addMoreCouponButton(){
        ImageButton moreCouponButton = (ImageButton)findViewById(R.id.button_more_coupon);
        moreCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPage.this, MyCoupon.class);
                startActivity(intent);

            }
        });
    }

    private void addProfile(){
        profile = (SimpleDraweeView)findViewById(R.id.imageview_profile);
        profile.setImageURI(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(profile.getContext());
                builder.setTitle(R.string.select_pic)
                        .setItems(R.array.picSelection, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i){
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");
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

    private void addEmail(){
        emailTextView = (TextView)findViewById(R.id.textview_email);
        emailTextView.setText(JoinUserInfo.getInstance().getEmail());
    }

    private void addName(){
        nameTextView = (TextView)findViewById(R.id.textview_nickname);
        nameTextView.setText(JoinUserInfo.getInstance().getName());
        nameTextView.setTypeface(null, Typeface.BOLD);
    }

    private void addBrandStar(){
        brandStarTextView = (TextView)findViewById(R.id.textview_brand_star);
        brandStarTextView.setText(getString(R.string.brand_star) + " " + JoinUserInfo.getInstance().getBrandStar() + "개");
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if(resCode == Activity.RESULT_OK){
            if(reqCode == REQUEST_CAMERA ){

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");
                //fileUri = Uri.parse(file.toString());
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

                uploadProfilePic();


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

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "MobyDick/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");
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

                uploadProfilePic();

            }else if (reqCode == REFRESH_VIEW){
                getPost();
            }

        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null){
            profile.setImageURI(Constants.appServerHost + "/ul/pf/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        profile.setImageURI(Constants.appServerHost + "/ul/pf/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 4;

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

    private void uploadProfilePic(){
        String profilePicPath = Environment.getExternalStorageDirectory() + File.separator + "MobyDick/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg";
        File file = new File(profilePicPath);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);

        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", file.getName(), requestFile);

        RequestBody uniqueId = RequestBody.create(MediaType.parse("multipart/form-data"), JoinUserInfo.getInstance().getUniqueId());

        Call<ResponseBody> call = apiService.uploadProfilePic(uniqueId, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200){
                    Fresco.getImagePipeline().evictFromCache(Uri.parse(Constants.appServerHost + "/ul/pf/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg"));
                    profile.refreshDrawableState();

                    profile.setImageURI(Constants.appServerHost + "/ul/pf/" + JoinUserInfo.getInstance().getUniqueId() + ".jpg");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private class LikedBrandsHorizontalAdapter extends RecyclerView.Adapter<LikedBrandsHorizontalAdapter.Holder>{

        @Override
        public LikedBrandsHorizontalAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_brand_column, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(LikedBrandsHorizontalAdapter.Holder holder, final int position) {
            holder.imageViewBrand.setImageURI(Constants.appServerHost + "/big/b/"+likedBrandList.get(position).brandId + "/thm.jpeg");
            holder.textViewBrandName.setText(likedBrandList.get(position).name);

            holder.imageViewBrand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyPage.this, BrandLikeAndDislikeCount.class);
                    intent.putExtra("brandId", likedBrandList.get(position).brandId);
                    intent.putExtra("brandName", likedBrandList.get(position).name);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return likedBrandList.size();
        }

        public class Holder extends RecyclerView.ViewHolder{
            public SimpleDraweeView imageViewBrand;
            public TextView textViewBrandName;

            public Holder(View itemView) {
                super(itemView);
                imageViewBrand = (SimpleDraweeView)itemView.findViewById(R.id.imageview_brand);
                textViewBrandName = (TextView)itemView.findViewById(R.id.textview_brand_name);
            }
        }
    }

    private class IssuedCouponHorizontalAdapter extends RecyclerView.Adapter<IssuedCouponHorizontalAdapter.Holder>{

        @Override
        public IssuedCouponHorizontalAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_issued_coupon_column, parent, false);
            return new IssuedCouponHorizontalAdapter.Holder(view);
        }


        @Override
        public void onBindViewHolder(IssuedCouponHorizontalAdapter.Holder holder, final int position) {
            holder.imageViewCoupon.setImageURI(currentCouponList.get(position).couponImage);
            holder.textViewCouponName.setText(currentCouponList.get(position).couponName);
            holder.imageViewCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyPage.this, IssuedCouponDetail.class);

                    intent.putExtra("couponId", currentCouponList.get(position).couponId);
                    intent.putExtra("serialCode", currentCouponList.get(position).serialCode);
                    intent.putExtra("couponProvider", currentCouponList.get(position).couponProvider);
                    intent.putExtra("couponName", currentCouponList.get(position).couponName);
                    intent.putExtra("couponImage", currentCouponList.get(position).couponImage);
                    intent.putExtra("startDate", currentCouponList.get(position).startDate);
                    intent.putExtra("endDate", currentCouponList.get(position).endDate);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return currentCouponList.size();
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public SimpleDraweeView imageViewCoupon;
            public TextView textViewCouponName;
            public Holder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                imageViewCoupon = (SimpleDraweeView)itemView.findViewById(R.id.imageview_coupon);
                textViewCouponName = (TextView)itemView.findViewById(R.id.textview_coupon_name);
            }

            @Override
            public void onClick(View view) {
                System.out.println(getLayoutPosition());
            }
        }
    }

}
