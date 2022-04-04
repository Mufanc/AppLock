package mufanc.tools.applock;

interface IAppLockManager {
    int[] handshake();
    void updatePackageList(in String[] packageList);
    String[] readPackageList();
}