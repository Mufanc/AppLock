package mufanc.tools.applock;

interface IAppLockManager {
    int[] handshake();
    void writePackageList(in String[] packageList);
    String[] readPackageList();
}