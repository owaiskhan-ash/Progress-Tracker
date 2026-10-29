import { Capacitor, registerPlugin } from '@capacitor/core';
import { LocalNotifications } from '@capacitor/local-notifications';

const ProjectTracker = registerPlugin('ProjectTracker');
const isNative = () => Capacitor.isNativePlatform();

window.__projectNotifications = {
  isNative,
  plugin: LocalNotifications,
  syncWidgetData: (projects) => isNative() ? ProjectTracker.syncWidgetData({ projects }) : Promise.resolve(),
};
