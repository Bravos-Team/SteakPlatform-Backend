docker compose up -d && sleep 10 &&
atlas migrate apply --env local &&
openssl genrsa -out private-key.pem 2048 &&
openssl rsa -in private-key.pem -pubout -out public-key.pem &&
echo 'Done'