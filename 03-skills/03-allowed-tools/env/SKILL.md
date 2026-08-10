---
name: env
description: Report installed Java and Maven versions
disable-model-invocation: true
allowed-tools: 
  - "Bash(java -version)"
  - "Bash(mvn -version)"
---
Check the installed Java and Maven versions and report the results using the below format.

```
🛠️ Local Environment
Java  : {java-version}
Maven : {maven-version}
```
