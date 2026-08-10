## Install and Configure the GitHub CLI

The GitHub CLI (`gh`) allows Claude to securely interact with your GitHub account.

### 1. Install the GitHub CLI

Download and install the GitHub CLI from:

https://cli.github.com/

### 2. Verify the installation

Open a terminal and run:

```bash
gh --version
```

If the installation is successful, the command displays the installed GitHub CLI version.

### 3. Authenticate with GitHub

Run the following command:

```bash
gh auth login
```

When prompted, choose the following options:

* **What account do you want to log into?** → `GitHub.com`
* **What is your preferred protocol for Git operations?** → `HTTPS`
* **Authenticate Git with your GitHub credentials?** → `Yes`
* **How would you like to authenticate GitHub CLI?** → `Paste an authentication token`

Paste the Personal Access Token you created in the previous step and press **Enter**.

### 4. Verify the authentication

Run the following command:

```bash
gh auth status
```

If the authentication is successful, the command displays your GitHub account information and confirms that you are logged in.
