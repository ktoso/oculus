#!/bin/sh

host='kmalawski@108.59.81.83'
knife solo prepare $host
knife solo cook $host nodes/newrelic-monitored.json

# slaves
host='kmalawski@23.236.57.222'
knife solo prepare $host
knife solo cook $host nodes/newrelic-monitored.json

host='kmalawski@108.59.86.163'
knife solo prepare $host
knife solo cook $host nodes/newrelic-monitored.json

host='kmalawski@162.222.176.34'
knife solo prepare $host
knife solo cook $host nodes/newrelic-monitored.json
