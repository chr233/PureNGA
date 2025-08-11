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
[![爱发电](https://img.shields.io/badge/爱发电-chr__-ea4aaa.svg?logo=github-sponsors)](https://afdian.com/@chr233)

## 公告

当前插件最佳适配 9.9.49 以及之前的版本

新版不一定会跟进

## 版本说明

|     类型     |                    特性                    |                  发行版链接                   |                     下载量                     |
| :----------: | :----------------------------------------: | :-------------------------------------------: | :--------------------------------------------: |
|  NGA 净化版  | 无需 Xposed 框架, 覆盖安装原版 App, 体积大 |    [![img][release_bundled]][link_bundled]    |    [![img][download_bundled]][link_bundled]    |
| 独立净化模块 |     依赖 Xposed 框架, 独立更新, 体积小     | [![img][release_standalone]][link_standalone] | [![img][download_standalone]][link_standalone] |

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

同时兼容 Lspatch, 支持 9.9.x 版本的 NGA 客户端, 理论向后兼容

## 捐赠

|               ![img][afdian_qr]                |                   ![img][bmac_qr]                   |                       ![img][usdt_qr]                       |
| :--------------------------------------------: | :-------------------------------------------------: | :---------------------------------------------------------: |
| ![爱发电][afdian_img] <br> [链接][afdian_link] | ![buy me a coffee][bmac_img] <br> [链接][bmac_link] | ![USDT][usdt_img] <br> `TW41eecZ199QK6zujgKP4j1cz2bXzRus3c` |

[afdian_qr]: https://raw.chrxw.com/chr233/master/afadian_qr.png
[afdian_img]: https://img.shields.io/badge/爱发电-@chr__-ea4aaa.svg?logo=github-sponsors
[afdian_link]: https://afdian.com/@chr233
[bmac_qr]: https://raw.chrxw.com/chr233/master/bmc_qr.png
[bmac_img]: https://img.shields.io/badge/buy%20me%20a%20coffee-@chr233-yellow?logo=buymeacoffee
[bmac_link]: https://www.buymeacoffee.com/chr233
[usdt_qr]: https://raw.chrxw.com/chr233/master/usdt_qr.png
[usdt_img]: https://img.shields.io/badge/USDT-TRC20-2354e6.svg?logo=bitcoin

## 设置菜单入口

1.16.0 以后支持从关于页打开插件菜单

| ![img3](app/src/main/res/drawable/tutorials3.webp) | ![img4](app/src/main/res/drawable/tutorials4.webp) |
| -------------------------------------------------- | -------------------------------------------------- |

全版本通用插件设置菜单位置

| ![img1](app/src/main/res/drawable/tutorials.webp) | ![img2](app/src/main/res/drawable/tutorials2.webp) |
| ------------------------------------------------- | -------------------------------------------------- |

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
[link_bundled]: https://github.com/chr233/PureNGA/releases/tag/NGA
[link_standalone]: https://github.com/Xposed-Modules-Repo/com.chrxw.purenga/releases
