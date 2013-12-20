#!/bin/sh

host='kmalawski@108.59.81.83'
knife solo prepare $host
knife solo cook $host nodes/master.json

# slaves
host='kmalawski@23.236.57.222'
knife solo prepare $host
knife solo cook $host nodes/master.json
