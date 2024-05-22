package xyz.mufanc.applock;

interface IAppLockService {
    Bundle handshake() = 16777114;
    List<String> getAvailableProviders() = 1;
}
