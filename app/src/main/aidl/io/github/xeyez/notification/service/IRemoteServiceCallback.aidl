// IRemoteServiceCallback.aidl
package io.github.xeyez.notification.service;

interface IRemoteServiceCallback {
    void onProgressService(int nowMillis);
    void onStopService();
}
