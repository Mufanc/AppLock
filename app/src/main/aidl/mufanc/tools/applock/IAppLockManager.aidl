package mufanc.tools.applock;

interface IAppLockManager {
    Bundle handshake() = 16777114;
    oneway void reboot() = 0;
    oneway void updateWhitelist(in String[] packageList) = 1;
}