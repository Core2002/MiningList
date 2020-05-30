# 挖掘榜插件

#### 介绍
本插件由钟小白Core一人开发，肝了一个晚上+一个下午
本插件基于Paper1.15.2核心开发，理论全版本支持
本插件和其他插件相比，有着bug极少，兼容性极强，性能更好等特点
举个例子，某论坛找的挖掘榜插件
1590743782863.png

1590744442127.png

linux环境下，文件路径鬼畜
1590743856527.png

1590744056278.png

数据使用yaml+玩家名称存储，玩家名称存储对正版玩家很不友好，如果玩家换id，则会出现分数丢失的情况
反编译插件得知，数据层使用Int进行存储，也就是说，当挖掘数超过2 的 31 次方 - 1 = 2147483648 - 1 = 2147483647 之后，插件会崩溃。
且排行榜无法由玩家关闭，有些玩家看着会强迫症
不仅如此，甚至还存在这这样的bug：
1590744409969.png
1590744609479.png

于是，我写了这个插件
上效果图：
1590744807533.png


存储方式使用了uuid+数字字符串存储
数据层使用字符串存储，理论支持无限位数数字存储
当正版玩

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
