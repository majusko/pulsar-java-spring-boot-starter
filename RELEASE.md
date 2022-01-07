# Release process

### 0. You need to configure your GPG key first
https://central.sonatype.org/publish/requirements/gpg/#installing-gnupg

### 1. On some machines you need to run this first

Because of: https://github.com/keybase/keybase-issues/issues/2798

```yaml
export GPG_TTY=$(tty)
```

### 2. Prepare your `my-settings.xml`

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>ossrh</id>
      <username>${env.SONATYPE_USERNAME}</username>
      <password>${env.SONATYPE_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

### 3. Execute release command

```yaml
mvn clean deploy -P release-sign-artifacts --settings my-settings.xml
```