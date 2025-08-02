# Alfresco TOTP authenticator
Time-based One-Time Password two factor authentication for Alfresco

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![Java CI](https://github.com/saidone75/alfresco-totp-authenticator/actions/workflows/build.yml/badge.svg)
![CodeQL](https://github.com/saidone75/alfresco-totp-authenticator/actions/workflows/codeql.yml/badge.svg)



[![Alfresco TOTP authenticator](totp-activate.gif)](https://vimeo.com/507443676)

Distributed for free without any warranty. Use at your own risk.

Tested with **Alfresco Community Edition 23.1** (for older Alfresco see previous releases).

[![Alfresco TOTP authenticator](totp-login.gif)](https://vimeo.com/507443676)

## Instructions
### Build
Get the sources:
```console
$ git clone https://github.com/saidone75/alfresco-totp-authenticator.git
```
build the JARs:
```console
$ cd alfresco-totp-authenticator
$ mvn package
[INFO] Scanning for projects...
[INFO]
[INFO] ----------< org.saidone:alfresco-totp-authenticator-platform >----------
[INFO] Building alfresco-totp-authenticator-platform Platform/Repository JAR Module 3.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------

[...]

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.420 s
[INFO] Finished at: 2023-12-26T08:44:13+01:00
[INFO] ------------------------------------------------------------------------
```
### Installation
- Stop Alfresco
- Install the JARs by copying them to the proper module location (depending on your Content Services installation), e.g.:
```
$ cp alfresco-totp-authenticator-platform-3.0.jar /opt/alfresco/modules/platform
$ cp alfresco-totp-authenticator-share-3.0.jar /opt/alfresco/modules/share
```
- Start Alfresco
### Configuration
- Log into Alfresco
- Go to your profile page and click on *TOTP settings* tab
- Click on *Activate TOTP* button to generate a secret and activate TOTP authentication
- Import the secret on your favourite OTP application (e.g. by scanning the QR Code), personally I use [andOTP](https://github.com/andOTP/andOTP) on Android but there's plenty of choices

Take a look at this short explanation video [here](https://vimeo.com/507443676).

### Pre-built JARs
Stable [releases](https://github.com/saidone75/alfresco-totp-authenticator/releases) include the related build.
Java CI successful workflows on [Actions](https://github.com/saidone75/alfresco-totp-authenticator/actions) will issue development artifacts.

## License
Copyright (c) 2021-2025 Saidone

Distributed under the GNU General Public License v3.0
