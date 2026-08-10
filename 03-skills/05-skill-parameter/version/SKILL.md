---
name: version
description: Check the installed version of a given tool
arguments: [tool]
argument-hint: "[tool-name]"
allowed-tools: Bash(* --version)
---

Run `$tool --version` and report the result using the below format.

```
🛠️ $tool version: {version}
```