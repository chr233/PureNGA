# PureNGA

![PureNGA](https://socialify.git.ci/chr233/PureNGA/image?description=1&forks=1&language=1&name=1&owner=1&pattern=Diagonal%20Stripes&stargazers=1&theme=Auto)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/de4c75db7487426285bf38f90ad94e6c)](https://www.codacy.com/gh/chr233/PureNGA/dashboard)
[![License](https://img.shields.io/github/license/chr233/PureNGA?logo=apache)](https://github.com/chr233/PureNGA/blob/master/license)
![GitHub last commit](https://img.shields.io/github/last-commit/chr233/PureNGA?logo=github)

[![GitHub Repo stars](https://img.shields.io/github/stars/chr233/PureNGA?logo=github)][repo_code]
[![GitHub Download](https://img.shields.io/github/downloads/chr233/PureNGA/total?logo=github)][repo_code]
[源码仓库][repo_code]

[![GitHub Repo stars](https://img.shields.io/github/stars/Xposed-Modules-Repo/com.chrxw.purenga?logo=github)][repo_xposed]
[![GitHub Download](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.chrxw.purenga/total?logo=github)][repo_xposed]
[Xposed 仓库][repo_xposed]

[![Bilibili](https://img.shields.io/badge/bilibili-Chr__-00A2D8.svg?logo=bilibili)](https://space.bilibili.com/5805394)
[![Steam](https://img.shields.io/badge/steam-Chr__-1B2838.svg?logo=steam)](https://steamcommunity.com/id/Chr_)

[![Steam](https://img.shields.io/badge/steam-donate-1B2838.svg?logo=steam)](https://steamcommunity.com/tradeoffer/new/?partner=221260487&token=xgqMgL-i)
[![爱发电][afdian_badge]][afdian_link]

## 公告

当前插件最佳适配 9.9.61 以及之前的版本

新版不一定会跟进

## 捐赠

| [<img src="https://raw.chrxw.com/PureNGA/main/.github/aifadian.png" width="250px">][afdian_link] | [<img src="https://raw.chrxw.com/PureNGA/main/.github/afadian-rank.png" width="250px">][afdian_link] |
| :----------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------: |

[afdian_badge]: https://img.shields.io/badge/爱发电-@chr__-ea4aaa.svg?logo=github-sponsors
[afdian_link]: https://afdian.com/@chr233

## 免责申明

- 本 App 完全免费, 如果在任何渠道付费取得, 请申请退款
- 本 App 不会篡改、修改和获取任何个人信息和论坛账号信息
- 本 APP 使用者因为违反本声明的规定而触犯中华人民共和国法律的, 一切后果自负, 作者不承担任何责任
- 请不要在任何渠道倒卖本 App, 免费传播不受限制
- 凡以任何方式直接、间接使用 APP 者, 视为自愿接受本声明的约束

- 本项目只在[爱发电][afdian_link]接受赞助, 咸鱼/淘宝均为倒卖, 请不要上当

### 以下为第三方倒卖

> 请不要在任何平台付费获取本应用, 本项目只在[爱发电][afdian_link]接受赞助

| ![1][shame1] | ![2][shame2] | ![3][shame3]) | ![4][shame4] |
| :----------: | :----------: | ------------- | ------------ |

[shame1]: https://raw.chrxw.com/PureNGA/main/.github/shame1.jpg
[shame2]: https://raw.chrxw.com/PureNGA/main/.github/shame2.jpg
[shame3]: https://raw.chrxw.com/PureNGA/main/.github/shame3.jpg
[shame4]: https://raw.chrxw.com/PureNGA/main/.github/shame4.jpg

## 版本说明

|     类型     |                    特性                    |                  发行版链接                   |                     下载量                     |
| :----------: | :----------------------------------------: | :-------------------------------------------: | :--------------------------------------------: |
|  NGA 净化版  | 无需 Xposed 框架, 覆盖安装原版 App, 体积大 |    [![img][release_bundled]][link_bundled]    |    [![img][download_bundled]][link_bundled]    |
| 独立净化模块 |     依赖 Xposed 框架, 独立更新, 体积小     | [![img][release_standalone]][link_standalone] | [![img][download_standalone]][link_standalone] |

## 网盘镜像

- 123 网盘 https://www.123pan.com/s/qYTvjv-0Autv.html 提取码 JEFR
- 夸克网盘 https://pan.quark.cn/s/6e7075812de3 提取码 NGyD

## FAQ

> Q: 会封号吗
>
> A: 原理是基于 Xposed 修改, 没有直接修改 APP 本身, 目前官方 App 没有校验客户端的机制
>
> Q: 安装整合版提示病毒怎么办
>
> A: 整合版原理是重新打包 Apk, 会导致打包的版本和官方版本签名不一致, 手动信任即可
>
> Q: 整合版分享到微信提示签名非法
>
> A: 整合版重新打包会影响 Apk 签名, 请复制链接分享或者使用独立插件版

## 主要功能

- [x] 开屏广告跳过
- [x] 信息流广告过滤
- [x] 屏蔽更新检测
- [x] 屏蔽内置浏览器
- [x] 假装分享
- [x] 自定义首页
- [x] 自动签到
- [x] 字体更换
- [x] 快捷方式支持
- [x] 游戏推荐广告位过滤

同时兼容 Lspatch, 支持 9.9.x 版本的 NGA 客户端, 理论向后兼容

## 设置菜单入口

1.16.0 以后支持从关于页打开插件菜单

| <img src="https://raw.chrxw.com/PureNGA/main/app/src/main/res/drawable-nodpi/tutorials3.webp" width="250px"> | !<img src="https://raw.chrxw.com/PureNGA/main/app/src/main/res/drawable-nodpi/tutorials4.webp" width="250px"> |
| ------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------- |

---

![Repobeats analytics image](https://repobeats.axiom.co/api/embed/4bf56a527045ee543205efea99a07e5d09bcd7c3.svg "Repobeats analytics image")

---

[![Star History Chart](https://api.star-history.com/svg?repos=chr233/PureNGA&type=Date)](https://star-history.com/#chr233/PureNGA&Date)

---

[repo_code]: https://github.com/chr233/PureNGA
[repo_xposed]: https://github.com/Xposed-Modules-Repo/com.chrxw.purenga
[release_bundled]: https://img.shields.io/github/v/release/chr233/PureNGA?logo=github&label=版本
[release_standalone]: https://img.shields.io/github/v/release/Xposed-Modules-Repo/com.chrxw.purenga?logo=github&label=版本
[download_bundled]: https://img.shields.io/github/downloads/chr233/PureNGA/total?logo=github&label=下载
[download_standalone]: https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.chrxw.purenga/total?logo=github&label=下载
[link_bundled]: https://github.com/chr233/PureNGA/releases/latest
[link_standalone]: https://github.com/Xposed-Modules-Repo/com.chrxw.purenga/releases/latest
