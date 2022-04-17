package mufanc.tools.applock;

interface IAppLockManager {
    int[] handshake() = 16777114;
    oneway void reboot() = 0;
    oneway void writePackageList(in String[] packageList) = 1;
    String[] importScopeFromOldVersion() = 1000;
}