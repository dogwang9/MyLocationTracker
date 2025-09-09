# MyLocationTracker

一个基于 **Android** 的位置追踪应用，集成了 **Google Maps SDK** 和 **ML Kit 扫码功能**，用于位置记录、路径规划、二维码位置分享等功能。  

## ✨ 功能特性

- **实时定位**  
  获取用户当前位置并在地图上显示。  

- **路径规划**  
  在地图上点击设置多个标记点（Marker），自动计算总路程。  

- **地理编码**  
  - 输入地理位置名称 → 转换为经纬度并显示在地图上  
  - 输入经纬度 → 转换为实际地址并显示  

- **二维码功能**  
  - 生成二维码：将当前位置（经纬度）转换为二维码，便于分享  
  - 扫描二维码：识别二维码中的位置信息，并在地图上显示  

- **UI 组件**  
  - `RecyclerView` 支持列表、网格、瀑布流布局  
  - 自定义扫码界面（300x300 扫描框）  
  - 动态地图标记、圆形覆盖物、边界限制  

## 📲 使用说明

1. 克隆项目
   ```bash
   git clone https://github.com/dogwang9/MyLocationTracker.git
   cd MyLocationTracker

2. 配置 Google Maps API Key
   在项目根目录创建 secrets.properties 文件，添加：
   ```bash
   MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY

3. 在 Android Studio 中打开项目，确保已安装：
    - Android SDK 34+
    - Google Play services
    - AndroidX 库

4. 运行应用，授予定位权限，即可开始使用。
