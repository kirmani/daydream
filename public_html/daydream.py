#! /usr/bin/env python
# -*- coding: utf-8 -*-
# vim:fenc=utf-8
#
# Copyright © 2015 Sean Kirmani <sean@kirmani.io>
#
# Distributed under terms of the MIT license.

import json
import os

from flask import Flask
from flask import jsonify
from flask import render_template
from flask import request

app = Flask(__name__)
app.secret_key = 'kirmani_io_secret_key'

TRIGGERS = 'triggers'

state = {TRIGGERS: 0}

@app.route('/')
def home():
  return 'Hello'

@app.route('/update', methods=['GET', 'POST'])
def update():
  if request.method == 'POST':
    print("POSITION UPDATE RECEIVED")
  elif request.method == 'GET':
    return jsonify(result=state)
  return 'Hello'

@app.route('/trigger', methods=['POST'])
def trigger():
  if request.method == 'POST':
    print("TRIGGER RECEIVED")
    state[TRIGGERS] = state[TRIGGERS] + 1
  return 'Hello'

if __name__ == '__main__':
  port = int(os.environ.get('PORT', 33507))
  app.run(host='0.0.0.0', port=port, debug=True)
