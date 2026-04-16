# Node.js 安装指南

## 下载 Node.js

1. 打开浏览器，访问 Node.js 官方网站：[https://nodejs.org](https://nodejs.org)
2. 在首页上，你会看到两个版本的下载选项：
   - **LTS (长期支持版)** - 推荐选择这个版本，它更加稳定
   - **Current (当前版)** - 包含最新特性，但可能不够稳定
3. 点击 LTS 版本的下载按钮，下载适合 Windows 系统的安装包（.msi 文件）

## 安装 Node.js

1. 双击下载好的 .msi 安装包，开始安装过程
2. 在安装向导中，点击 "Next" 按钮
3. 阅读并接受许可协议，然后点击 "Next"
4. 选择安装位置（默认位置即可），然后点击 "Next"
5. 在 "Custom Setup" 页面，保持默认选项，然后点击 "Next"
6. 点击 "Install" 按钮开始安装
7. 安装完成后，点击 "Finish" 按钮

## 验证安装

1. 打开命令提示符（CMD）或 PowerShell
2. 运行以下命令检查 Node.js 版本：
   ```
   node -v
   ```
3. 运行以下命令检查 npm 版本：
   ```
   npm -v
   ```

如果两个命令都能显示版本号，说明 Node.js 安装成功。

## 配置 npm 镜像（可选）

为了加快依赖包的下载速度，你可以配置 npm 使用国内镜像：

```
npm config set registry https://registry.npmmirror.com
```

这样，后续使用 npm 安装依赖时会更快。