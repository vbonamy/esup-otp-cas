Works on CAS V5.0.*

## Config

In cas.properties

```
# MFA - google authenticator
cas.authn.mfa.globalProviderId=mfa-esupotp

##
# Esup Otp Authentication
#
cas.mfa.esupotp.rank =0
cas.mfa.esupotp.urlApi =http://esup-otp-cifren.c9users.io:8081
cas.mfa.esupotp.usersSecret =changeit
cas.mfa.esupotp.apiPassword =changeit

# Add translations, you will need to check what are the default from CAS "Message Bundles" properties
cas.messageBundle.baseNames=classpath:custom_messages,classpath:messages,classpath:esupotp_message
```

In cas/build.gradle

``` groovy
dependencies {
    compile("org.springframework.boot:spring-boot-devtools:${project.'springboot.version'}")
    compile "org.apereo.cas:cas-server-webapp:${project.'cas.version'}@war"
    compile 'org.json:json:20160810'
    providedCompile 'javax.servlet:javax.servlet-api:3.0.1'
    // VERSION is either commit#number or releases#version
    // Becareful: Conflict with gauth-mfa package
    compile "com.github.cifren:CasEsupOtp:VERSION"
}
```

    TIPS: Look for https://jitpack.io/#cifren/CasEsupOtp and check the available version you can use

In log4j2.xml
```
<AsyncLogger name="org.apereo.cas.adaptors.esupotp" level="debug" additivity="false" includeLocation="true">
    <AppenderRef ref="casConsole"/>
    <AppenderRef ref="casFile"/>
</AsyncLogger>
```
