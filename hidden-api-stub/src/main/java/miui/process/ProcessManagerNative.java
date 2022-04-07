package miui.process;

import android.os.Binder;
import android.os.IBinder;

public abstract class ProcessManagerNative extends Binder implements IProcessManager {
    public static IProcessManager asInterface(IBinder binder) {
        throw new RuntimeException("stub!");
    }
}
