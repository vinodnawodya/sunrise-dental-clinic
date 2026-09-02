#!/usr/bin/env bash
# Standalone REST API client for the Sunrise Dental Clinic system.
# Demonstrates that the API is reachable independently of the Thymeleaf web
# UI (the "distributed application / web services" requirement) - this
# script is a separate process using only curl + HTTP Basic auth, no
# browser session involved.
#
# Usage: ./scripts/api-client-demo.sh [username] [password] [base_url]
set -euo pipefail

USERNAME="${1:-admin}"
PASSWORD="${2:-Sunrise123!}"
BASE_URL="${3:-http://localhost:8080}"

echo "== 1. Create a new appointment =="
CREATE_RESPONSE=$(curl -s -u "$USERNAME:$PASSWORD" \
  -H "Content-Type: application/json" \
  -d '{"patientId":1,"dentistId":1,"treatmentId":2,"appointmentDate":"2026-09-10","appointmentTime":"10:00:00"}' \
  "$BASE_URL/api/appointments")
echo "$CREATE_RESPONSE"

APPOINTMENT_NUMBER=$(echo "$CREATE_RESPONSE" | grep -o '"appointmentNumber":"[^"]*"' | head -1 | sed 's/.*:"//;s/"//')
echo
echo "Created appointment: $APPOINTMENT_NUMBER"
echo

echo "== 2. Fetch that appointment back by number =="
curl -s -u "$USERNAME:$PASSWORD" "$BASE_URL/api/appointments/$APPOINTMENT_NUMBER"
echo
echo

echo "== 3. Generate/fetch its bill =="
curl -s -u "$USERNAME:$PASSWORD" "$BASE_URL/api/bills/$APPOINTMENT_NUMBER"
echo
