# hello-app

Set your GitHub OAuth credentials as environment variables, then run with Java 27:

```sh
export GITHUB_CLIENT_ID='your-client-id'
export GITHUB_CLIENT_SECRET='your-client-secret'
./gradlew bootRun
```

Open http://localhost:8080 for the welcome message and **Login with GitHub** button.
Successful OAuth login opens the protected `/landing.html` profile page, showing
your GitHub avatar, username, name, ID, email when available, and a sign-out button.

When running from an IDE, add these variables to the run configuration.
The OAuth application's
callback URL for local development is `http://localhost:8080/login/oauth2/code/github`.
Both pages are rendered on the server with Thymeleaf. The landing page uses
JavaScript to fetch a cat fact from the authenticated `/api/cat-fact` endpoint.

## Environment variables with mise

Start with the [mise getting started guide](https://mise.jdx.dev/getting-started.html)
to install and set up mise.

As an alternative to exporting variables manually, create `mise.local.toml` in the
project root, beside `mise.toml`:

```toml
[env]
GITHUB_CLIENT_ID = "your-client-id"
GITHUB_CLIENT_SECRET = "your-client-secret"
```

`mise.local.toml` is ignored by Git. Keep your actual credentials in that local
file; the shared `mise.toml` configures Java 27.

From the project root, trust the configuration, install the configured tools, and
start the application with the environment variables loaded:

```sh
mise trust
mise install
mise exec -- ./gradlew bootRun
```

When launching directly from an IDE, configure the same variables in its run
configuration; the command above applies them to the application started by mise.
