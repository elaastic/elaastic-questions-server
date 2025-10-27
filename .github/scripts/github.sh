#!/usr/bin/env sh

# Helper that calls the GitHub API via curl.
# arg1: The HTTP method.
# arg2: The URL to call.
# arg3: The authentication token.
# argN: Arguments to pass to curl.
function curl_gh() {
    local method="$1"; shift
    local url="https://api.github.com$1"; shift
    local token="$1"; shift

    curl -fsS --request $method --url "$url" \
        --header "Accept: application/vnd.github+json" \
        --header "Authorization: Bearer $token" \
        --header "X-GitHub-Api-Version: 2022-11-28" \
        "$@"
}

#region Authentication

# Produces a GitHub App JWT valid for 30 seconds, with the given Client ID and RSA private key.
# arg1: Client ID of the GitHub App.
# arg2: Path to private RSA key.
# returns: The generated JWT.
function generate_gh_app_jwt() {
    local client_id="$1"
    local private_key_path="$2"

    local now_seconds=$(date +%s)
    local issued_at=$(( $now_seconds - 10 )) # Allow 10s of clock drift
    local expires_at=$(( $now_seconds + 30 )) # Valid for 30 seconds

    local jwt_header="{\"alg\":\"RS256\",\"typ\":\"JWT\"}"
    local jwt_payload="{\"iat\":$issued_at,\"exp\":$expires_at,\"iss\":\"$client_id\"}"

    local enc_jwt_header=$(echo -n "$jwt_header" | base64 | tr '+/' '-_' | tr -d '\n=')
    local enc_jwt_payload=$(echo -n "$jwt_payload" | base64 | tr '+/' '-_' | tr -d '\n=')

    local jwt_signature=$(
        echo -n "$enc_jwt_header.$enc_jwt_payload" | \
        openssl dgst -sha256 -sign $private_key_path -binary | \
        openssl base64 | tr '+/' '-_' | tr -d '\n='
    )

    echo -n "$enc_jwt_header.$enc_jwt_payload.$jwt_signature"
}

# Produces an authentication token for the GitHub App valid for 1 hour within the elaastic organization.
# arg1: A valid JWT token for the GitHub App. See `generate_gh_app_jwt`.
# arg2: Installation ID.
# returns: The authentication token issued by GitHub.
function authenticate_gh_app() {
    local jwt="$1"
    local inst_id="$2"

    curl_gh POST "/app/installations/$inst_id/access_tokens" "$jwt" | jq -r .token
}

#endregion

#region Check Run

# Creates a check run.
# arg1: Authentication token.
# arg2: Repository (in `<owner>/<repo>` format).
# arg3: Check run name.
# arg4: Target commit ID.
# arg5: GitLab project URL.
# returns: Check run ID.
function create_check_run() {
    local auth_token="$1"
    local repository="$2"
    local check_run_name="$3"
    local check_run_target="$4"
    local gitlab_project_url="$5"

    local payload="{\"name\":\"$check_run_name\",\"head_sha\":\"$check_run_target\",\"details_url\":\"$gitlab_project_url/-/pipelines\"}"

    curl_gh POST "/repos/$repository/check-runs" "$auth_token" -d "$payload" | jq -r .id
}

# Updates a check run's details.
# arg1: Authentication token.
# arg2: Repository (in `<owner>/<repo>` format).
# arg3: Check run ID.
# arg4: GitLab project URL.
# arg5: GitLab CI/CD pipeline ID.
function update_check_run_details() {
    local auth_token="$1"
    local repository="$2"
    local check_run_id="$3"
    local gitlab_project_url="$4"
    local gitlab_pipeline_id="$5"

    local payload="{\"details_url\":\"$gitlab_project_url/-/pipelines/$gitlab_pipeline_id\",\"external_id\":\"$gitlab_pipeline_id\"}"

    curl_gh PATCH "/repos/$repository/check-runs/$check_run_id" "$auth_token" -d "$payload"
}

# Updates a check run's status to `in_progress`.
# arg1: Authentication token.
# arg2: Repository (in `<owner>/<repo>` format).
# arg3: Check run ID.
function start_check_run() {
    local auth_token="$1"
    local repository="$2"
    local check_run_id="$3"

    local payload="{\"status\":\"in_progress\"}"

    curl_gh PATCH "/repos/$repository/check-runs/$check_run_id" "$auth_token" -d "$payload"
}

# Updates a check run's status to `completed` with a given conclusion.
# Conclusion can be one of: `action_required`, `cancelled`, `failure`, `neutral`, `success`, `skipped`, `timed_out`
# arg1: Authentication token.
# arg2: Repository (in `<owner>/<repo>` format).
# arg3: Check run ID.
# arg4: Conclusion.
function finish_check_run() {
    local auth_token="$1"
    local repository="$2"
    local check_run_id="$3"
    local conclusion="$4"

    local payload="{\"conclusion\":\"$conclusion\"}"

    curl_gh PATCH "/repos/$repository/check-runs/$check_run_id" "$auth_token" -d "$payload"
}

#endregion
