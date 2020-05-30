# 挖掘榜插件

#### Description
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

#### Software Architecture
Software architecture description

#### Installation

1.  xxxx
2.  xxxx
3.  xxxx

#### Instructions

1.  xxxx
2.  xxxx
3.  xxxx

#### Contribution

1.  Fork the repository
2.  Create Feat_xxx branch
3.  Commit your code
4.  Create Pull Request


#### Gitee Feature

1.  You can use Readme\_XXX.md to support different languages, such as Readme\_en.md, Readme\_zh.md
2.  Gitee blog [blog.gitee.com](https://blog.gitee.com)
3.  Explore open source project [https://gitee.com/explore](https://gitee.com/explore)
4.  The most valuable open source project [GVP](https://gitee.com/gvp)
5.  The manual of Gitee [https://gitee.com/help](https://gitee.com/help)
6.  The most popular members  [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
