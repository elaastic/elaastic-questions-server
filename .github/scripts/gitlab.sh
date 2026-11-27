#!/usr/bin/env sh

# Starts a section that will be collapsible in GitLab CI/CD logs.
# Reference: https://docs.gitlab.com/ci/jobs/job_logs/#expand-and-collapse-job-log-sections
# arg1: section key (alphanumeric plus underscores, dashes and dots only).
# arg2: header of the section shown in the logs.
function section_start() {
    local section_key="${1}"
    local section_header="${2:-$section_key}"

    echo -e "section_start:`date +%s`:${section_key}\r\e[0K${section_header}"
}

# Closes a section previously started by `section_start`.
# Reference: https://docs.gitlab.com/ci/jobs/job_logs/#expand-and-collapse-job-log-sections
# arg1: section key (must match the one used to start the section).
function section_end() {
    local section_key="${1}"

    echo -e "section_end:`date +%s`:${section_key}\r\e[0K"
}
