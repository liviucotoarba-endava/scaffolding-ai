---
name: jenkins
description: Inspect Endava Jenkins with the locally configured Jenkins CLI.
---

# Jenkins

- Use the local Jenkins CLI JAR for all Jenkins operations. Connect to `https://jenkins.endava.com` over HTTPS/WebSocket.
- Authenticate with `-auth @/Users/liviucotoarba/.config/jenkins/endava.auth`; the configured Jenkins user is `lcotoarba`. Never print, copy, or expose the credentials file or API token.
- Default to read-only commands such as listing jobs, reading job configuration, and viewing build output or status.
- Before running a command that starts or cancels a build, changes job configuration, or otherwise writes to Jenkins, ask the user and wait for permission.
