package miui.process;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

@SuppressLint("ParcelCreator")
public class ProcessCloudData implements Parcelable {

    public ProcessCloudData() {
        throw new RuntimeException("stub!");
    }

    public void setCloudWhiteList(List<String> mCloudWhiteList) {
        throw new RuntimeException("stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("stub!");
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        throw new RuntimeException("stub!");
    }
}