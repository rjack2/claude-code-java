# GitHub Workflows

This project uses two GitHub Actions workflows.

## 1. `claude.yml`

This workflow enables Claude Code to work on GitHub issues and respond to `@claude` requests.

```yaml
name: Claude Code

on:
  issue_comment:
    types: [created]
  pull_request_review_comment:
    types: [created]
  issues:
    types: [opened, assigned]
  pull_request_review:
    types: [submitted]

jobs:
  claude:
    if: |
      (github.event_name == 'issue_comment' && contains(github.event.comment.body, '@claude')) ||
      (github.event_name == 'pull_request_review_comment' && contains(github.event.comment.body, '@claude')) ||
      (github.event_name == 'pull_request_review' && contains(github.event.review.body, '@claude')) ||
      (github.event_name == 'issues' && (contains(github.event.issue.body, '@claude') || contains(github.event.issue.title, '@claude')))

    runs-on: ubuntu-latest

    permissions:
      contents: read
      pull-requests: read
      issues: read
      id-token: write
      actions: read

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 1

      - name: Set up JDK 25
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "25"

      - name: Verify Java installation
        run: |
          java -version
          mvn -version

      - name: Run Claude Code
        id: claude
        uses: anthropics/claude-code-action@v1
        with:
          claude_code_oauth_token: ${{ secrets.CLAUDE_CODE_OAUTH_TOKEN }}

          additional_permissions: |
            actions: read

          claude_args: >-
            --allowedTools
            "Bash(mvn *),Bash(java *),Bash(git *),Bash(gh *)"
```

## 2. `claude-code-review.yml`

This workflow automatically reviews Pull Requests for potential issues.

```yaml
name: Claude Code Review

on:
  pull_request:
    types: [opened, synchronize, ready_for_review, reopened]

jobs:
  claude-review:
    if: github.actor != 'claude[bot]'
    runs-on: ubuntu-latest

    permissions:
      contents: read
      pull-requests: write
      issues: read
      id-token: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 1

      - name: Run Claude Code Review
        uses: anthropics/claude-code-action@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          claude_code_oauth_token: ${{ secrets.CLAUDE_CODE_OAUTH_TOKEN }}
          claude_args: '--allowedTools "Bash(gh pr *)"'
          prompt: |
            You are an experienced senior software engineer performing a pull request review.
            Keep the review concise and actionable.

            Step 1: Run: gh pr diff ${{ github.event.pull_request.number }}
            Step 2: Post review using: gh pr comment ${{ github.event.pull_request.number }} --body "..."

            Only report issues related to:
            - Security vulnerabilities
            - Potential bugs
            - Data loss risks
            - Unhandled errors

            Focus on issues that are likely to occur during normal application usage.
            Do not speculate about unlikely race conditions, theoretical edge cases, or defensive improvements.
            Only report issues when you have high confidence they are real.

            If you do not identify any issues, post:
            "LGTM. I did not identify any issues in this PR."
```

## Notes

* Use **regular Pull Request conversation comments** (not inline review comments) when interacting with Claude.
* The review workflow excludes `claude[bot]` to prevent review loops.
* Refer to the official documentation for additional configuration options:

  [https://github.com/anthropics/claude-code-action/blob/main/docs/usage.md](https://github.com/anthropics/claude-code-action/blob/main/docs/usage.md)