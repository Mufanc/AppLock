package miui.process;

import android.os.IInterface;
import android.os.RemoteException;

public interface IProcessManager extends IInterface {
    void updateCloudData(ProcessCloudData processCloudData) throws RemoteException;
}
