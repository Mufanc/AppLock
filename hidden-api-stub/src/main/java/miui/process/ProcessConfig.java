package miui.process;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("Unused")
public class ProcessConfig implements Parcelable {

    public static final int KILL_LEVEL_TRIM_MEMORY = 0;

    protected ProcessConfig(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProcessConfig> CREATOR = new Creator<ProcessConfig>() {
        @Override
        public ProcessConfig createFromParcel(Parcel in) {
            return new ProcessConfig(in);
        }

        @Override
        public ProcessConfig[] newArray(int size) {
            return new ProcessConfig[size];
        }
    };
}
