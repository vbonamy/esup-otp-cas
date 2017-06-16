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
```

In cas/build.gradle

``` groovy
dependencies {
    compile("org.springframework.boot:spring-boot-devtools:${project.'springboot.version'}")
    compile "org.apereo.cas:cas-server-webapp:${project.'cas.version'}@war"
    compile 'org.json:json:20160810'
    providedCompile 'javax.servlet:javax.servlet-api:3.0.1'
    compile "org.apereo.cas:cas-server-support-gauth:${project.'cas.version'}"
}
```