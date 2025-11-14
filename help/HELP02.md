# MojoNotFoundException
#### 由 Benjamin Bentmann 创建，最后修改于 2009 年 10 月 16 日
当你引用了在插件中不存在的目标时，就会发生此错误。造成这种情况的常见原因有：

1. 目标名称中有简单的拼写错误，无论是在命令行上还是在 POM 的 <goal> 元素中。
2. 你尝试使用的目标仅在插件的较新版本中可用。

在后一种情况下，你应仔细检查相关目标的文档，并在需要时更新 POM 中的插件版本。许多常用 Maven 插件的文档可以通过我们的插件索引访问。

提示：如果你不想或无法浏览插件的在线文档，也可以使用如下命令了解更多信息：
```bash
mvn help:describe -Dplugin=org.apache.maven.plugins:maven-antrun-plugin
```
以了解插件及其目标的更多信息。许多较新的插件版本已经集成了此帮助，因此对于这些插件也可以使用：
```bash
mvn org.apache.maven.plugins:maven-antrun-plugin:help
```