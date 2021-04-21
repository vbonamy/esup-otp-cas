Works on CAS V6.3.3

## Config

In esupotp.properties

```
##
# Esup Otp Authentication
#
esupotp.rank=0
esupotp.urlApi=http://my-api.com:8081
esupotp.usersSecret=changeit
esupotp.apiPassword=changeit
esupotp.byPassIfNoEsupOtpMethodIsActive=true
esupotp.trustedDeviceEnabled=true
esupotp.isDeviceRegistrationRequired=false
```

In cas.properties

```
# MFA - google authenticator
cas.authn.mfa.globalProviderId=mfa-esupotp

# Add translations, you will need to check what are the default from CAS "Message Bundles" properties
cas.messageBundle.baseNames=classpath:custom_messages,classpath:messages,classpath:esupotp_message
```

In cas/build.gradle

``` groovy
...
repositories {
  ...
  maven {
        url "https://jitpack.io"
    }
}
...

dependencies {
    ...
    implementation "com.github.EsupPortail:esup-otp-cas:ad586a2475"
}
```

    TIPS: Look for https://jitpack.io/#EsupPortail/esup-otp-cas and check the available version you can use - here ad586a2475 is ok with 6.3.3 but maybe it's not the last one

In log4j2.xml
```
<AsyncLogger name="org.esupportail.cas.adaptors.esupotp" level="debug" additivity="false" includeLocation="true">
    <AppenderRef ref="casConsole"/>
    <AppenderRef ref="casFile"/>
</AsyncLogger>
```
