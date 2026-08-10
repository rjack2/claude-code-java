## Install the Claude GitHub App

In this step, you will connect Claude with your GitHub repository. Claude will install the GitHub App and configure the required GitHub Actions workflows so it can automate development tasks such as creating pull requests, reviewing code, and responding to GitHub events.

### 1. Create the repository

On GitHub, create a new repository named `students-manager`. Check **Add a README file** so the repository has an initial commit and a default branch. Do not add any other code. We will build the application from scratch in later lectures, entirely through GitHub issues and Claude.

### 2. Launch Claude CLI

Open a terminal and start the Claude CLI.

### 3. Run the installation skill

Execute the following command:

```text
/install-github-app
```

### 4. Select the repository

When prompted, enter the URL of the `students-manager` repository you just created.

### 5. Install the Claude GitHub App

The Claude CLI opens your browser to install the GitHub App.

If the installation page does not open automatically, visit:

https://github.com/apps/claude

Complete the installation, then return to the terminal and press **Enter** to continue.

### 6. Configure GitHub Actions

When prompted, select:

```text
Set up GitHub Actions workflows
```

This step configures the required GitHub Actions workflows, allowing Claude to securely integrate with your repository and participate in your CI/CD pipeline.

Once configured, Claude can:

* Create pull requests
* Review and comment on pull requests
* Respond to GitHub events
* Work on tasks assigned through GitHub workflows

### 7. Authorize Claude

When prompted, select:

```text
Create a long-lived token with your Claude subscription
```

Your browser opens the Claude authorization page.

Complete the authorization process and return to the terminal.

### 8. Merge the workflow changes

After the setup is complete, Claude creates a new branch containing the required GitHub workflow files.

Review the changes, then merge the branch into your main branch (`main` or `master`).

Your GitHub repository is now fully integrated with Claude and ready for GitHub-based AI workflows.
