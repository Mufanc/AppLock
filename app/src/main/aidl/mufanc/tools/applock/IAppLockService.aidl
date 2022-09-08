package mufanc.tools.applock;

interface IAppLockService {
    Bundle handshake() = 16777114;
    oneway void reboot() = 0;
    oneway void updateConfigs(in Bundle bundle) = 1;
}
