# read the workflow template
WORKFLOW_TEMPLATE=$(cat .github/workflow/workflow-template.yaml)
dir_array=("message-service")

# iterate each route in routes directory
for ROUTE in "${dir_array[@]}"; do
    echo "generating workflow for routes/${ROUTE}"

    # replace template route placeholder with route name
    WORKFLOW=$(echo "${WORKFLOW_TEMPLATE}" | sed "s/{{ROUTE}}/${ROUTE}/g")

    # save workflow to .github/workflows/{ROUTE}
    echo "${WORKFLOW}" > .github/workflows/${ROUTE}.yaml
done
