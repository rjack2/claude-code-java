## Create a GitHub Personal Access Token

Claude requires a GitHub Personal Access Token (PAT) to access and modify your repositories.

### 1. Open the Personal Access Token page

Go to:

https://github.com/settings/personal-access-tokens

### 2. Generate a new token

Click **Generate new token**.

### 3. Configure the token

* **Token name:** Enter a meaningful name (for example, `Claude GitHub Integration`).
* **Repository access:** Select **All repositories**.

### 4. Grant repository permissions

Assign **Read and Write** access for the following permissions:

* Contents
* Issues
* Secrets
* Pull requests
* Workflows

### 5. Generate the token

Click **Generate token**.

> **Important:** GitHub displays the token only once. Copy it immediately and store it in a secure location. You will need this token when configuring Claude's GitHub integration.
