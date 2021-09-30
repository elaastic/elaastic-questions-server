docker run --rm \
    --volume $PWD/.:/data \
    --user $(id -u):$(id -g) \
    --env JOURNAL=jose \
    openjournals/paperdraft