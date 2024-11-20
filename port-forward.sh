kubectl port-forward svc/camunda-zeebe-gateway 26500:26500  &
kubectl port-forward svc/camunda-zeebe  9600:9600 &
kubectl port-forward svc/camunda-operate 8081:80 &
kubectl port-forward svc/camunda-tasklist  8082:80 &
kubectl port-forward svc/camunda-identity 8080:80  &
kubectl port-forward svc/camunda-keycloak  18080:80 &