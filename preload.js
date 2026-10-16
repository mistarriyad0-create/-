const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  getAppVersion: () => ipcRenderer.invoke('app-version'),
  openExternal: (url) => ipcRenderer.invoke('open-external', url),
  platform: process.platform
});
