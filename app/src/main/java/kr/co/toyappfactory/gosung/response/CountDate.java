package kr.co.toyappfactory.gosung.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chiduk on 2016. 6. 29..
 */
public class CountDate implements Parcelable {
    public int year;
    public int month;
    public int day;
    public int likeCount;


    public CountDate(Parcel in){
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.likeCount = in.readInt();
    }

    public CountDate(int year, int month, int day, int likeCount){
        this.year = year;
        this.month = month;
        this.day = day;
        this.likeCount = likeCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final Parcelable.Creator<CountDate> CREATOR = new Parcelable.Creator<CountDate>(){
        public CountDate createFromParcel(Parcel in){
            return new CountDate(in);
        }

        public CountDate[] newArray(int size){
            return new CountDate[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(likeCount);
    }
}

