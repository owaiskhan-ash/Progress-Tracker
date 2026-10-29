import { Capacitor } from '@capacitor/core';
import { LocalNotifications } from '@capacitor/local-notifications';

window.__projectNotifications = {
  isNative: () => Capacitor.isNativePlatform(),
  plugin: LocalNotifications,
};
