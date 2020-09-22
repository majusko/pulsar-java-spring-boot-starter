# Release process

### 1. On some machines you need to run this first

Because of: https://github.com/keybase/keybase-issues/issues/2798

```yaml
export GPG_TTY=$(tty)
```

### 2. Prepare your `my-settings.xml`

```xml
  <servers>
    <server>
      <id>ossrh</id>
      <username>${env.SONATYPE_USERNAME}</username>
      <password>${env.SONATYPE_PASSWORD}</password>
    </server>
  </servers>
```

### 3. Execute release command

```yaml
mvn clean deploy -P release-sign-artifacts --settings my-settings.xml
```