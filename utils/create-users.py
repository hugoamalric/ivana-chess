#!/bin/python3
#
# This script is used to create some users.
#
# Usage: ./utils/create-users.py <base url> <count>

import sys

import requests

url = sys.argv[1]
count = int(sys.argv[2])
for i in range(0, count):
    pseudo = "user" + str(i + 1)
    email = pseudo + '@ivanachess.loc'
    password = pseudo
    data = {
        'pseudo': pseudo,
        'email': email,
        'password': password
    }
    response = requests.post(url + '/user/signup', json=data)
    if response.status_code != 201:
        print(
            'Unable to create user \'' + pseudo + '\':', response.json(), '(' + str(response.status_code) + ')',
            file=sys.stderr
        )
    else:
        print('User \'' + pseudo + '\' created')
