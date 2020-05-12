package kr.co.toyappfactory.gosung.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by chiduk on 2016. 6. 29..
 */
public class CountList extends ArrayList<CountDate> implements Parcelable {


    public CountList(){

    }

    public CountList(Parcel in){
        int size = in.readInt();

        for(int i = 0; i < size; i++){
            CountDate date = new CountDate(in.readInt(), in.readInt(), in.readInt(), in.readInt());
            this.add(date);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final Parcelable.Creator<CountList> CREATOR = new Parcelable.Creator<CountList>(){
        public CountList createFromParcel(Parcel in){
            return new CountList(in);
        }

        public CountList[] newArray(int size){
            return new CountList[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int size = this.size();

        dest.writeInt(size);

        for(int i = 0; i < size; i++){
            CountDate date = this.get(i);

            dest.writeInt(date.year);
            dest.writeInt(date.month);
            dest.writeInt(date.day);
            dest.writeInt(date.likeCount);
        }
    }
}



