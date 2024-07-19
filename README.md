# Bypass Suite

**Bypass Suite** 是一个 Burp Suite 插件，旨在帮助安全研究人员绕过 Web 应用防火墙 (WAF) 的防御机制。该插件提供了一些有用的功能，可以用于测试和绕过 WAF 检测，包括 Unicode 编解码、关键词拆分、插入脏数据和 随机大小写。

## 功能

- **Unicode Encode**: 将选中的文本转换为 Unicode 编码形式。
- **Unicode Decode**: 将 Unicode 编码的文本转换回原始形式。
- **Split Keyword**: 将选中的文本按照特定规则进行拆分，例如将 `com.wec.kkkk` 拆分为 `'co'+'m.'w'+'ec.k'+'kkk'`。
- **Insert Garbage Data**: 在请求中插入随机生成的键值对，用户可以指定要插入的键值对数量。
- **Random Case**: 对选中的文本进行 进行随机大小写变换。

## 安装

1. 下载[Bypass Suite JAR 文件]([https://github.com/Conan924/Bypass-Suite/releases/download/V1.0/BypassSuite-all.jar])。
2. 打开 Burp Suite。
3. 转到 "Extender" 标签页，然后选择 "Extensions"。
4. 点击 "Add" 按钮。
5. 在弹出的对话框中，选择 "Java" 作为扩展类型，并浏览到下载的 JAR 文件。
6. 点击 "Next" 并完成安装。

## 使用方法

1. 在 Burp Suite 中打开一个请求包。
2. 右键单击请求包，在弹出的上下文菜单中选择 "Bypass Suite"。
3. 根据需要选择一个操作：
   - **Unicode Encode**: 将选中的文本编码为 Unicode。
   - **Unicode Decode**: 解码 Unicode 编码的文本。
   - **Split Keyword**: 拆分选中的文本。
   - **Insert Garbage Data**: 插入随机生成的键值对。弹出对话框要求输入要插入的键值对数量。
   - **Random Case**: 对选中的文本进行 进行随机大小写变换。

4. 插件会自动处理选中的文本并更新请求包。
