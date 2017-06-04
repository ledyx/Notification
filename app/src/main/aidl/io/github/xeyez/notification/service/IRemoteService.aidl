// IRemoteService.aidl
package io.github.xeyez.notification.service;

import io.github.xeyez.notification.service.IRemoteServiceCallback;

interface IRemoteService {
    boolean registerCallback(IRemoteServiceCallback callback);
    boolean unregisterCallback(IRemoteServiceCallback callback);
}
