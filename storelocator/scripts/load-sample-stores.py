#!/usr/bin/env python
#
# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Loads sample store data into a spanner database.
import os
import argparse

from google.cloud import spanner

parser = argparse.ArgumentParser()
parser.add_argument('--delete', help='deletes existing data',
                    action='store_true')


def safe_type_cast(a):
    try:
        return int(a)
    except (ValueError, TypeError):
        pass
    try:
        return float(a)
    except (ValueError, TypeError):
        pass
    return a


def drop_newline(a):
    return a.replace("\n", "")


if "GOOGLE_APPLICATION_CREDENTIALS" not in os.environ:
  print("Environment variable GOOGLE_APPLICATION_CREDENTIALS not set")
  exit(1)

if "STORE_LOCATOR_SPANNER_INSTANCE_ID" not in os.environ:
  print("Environment variable STORE_LOCATOR_SPANNER_INSTANCE_ID not set")
  exit(1)

if "STORE_LOCATOR_SPANNER_DATABASE" not in os.environ:
  print("Environment variable STORE_LOCATOR_SPANNER_DATABASE not set")
  exit(1)

DATAFILE = 'scripts/sample-stores.csv'
if "DATAFILE" in os.environ:
  DATAFILE = os.environ['DATAFILE']

with open(DATAFILE, 'r') as file:
  columns = drop_newline(file.readline()).split(',')
  types = drop_newline(file.readline()).split(',')
  lines = file.readlines()

lines = [[safe_type_cast(drop_newline(field)) for field in line.split(',')] for
         line in lines]

spanner_client = spanner.Client()
instance = spanner_client.instance(
    os.environ['STORE_LOCATOR_SPANNER_INSTANCE_ID'])
database = instance.database(os.environ['STORE_LOCATOR_SPANNER_DATABASE'])

args = parser.parse_args()

if args.delete:
  database.drop()

if not database.exists():
  database.create()

  s = 'CREATE TABLE stores ('\
      + ", ".join([pair[0] + ' ' + pair[1] for pair in zip(columns, types)])\
      + ') PRIMARY KEY (latitude, longitude)'

  print(s)
  operation = database.update_ddl([s])

  print('Waiting for operation to complete...')
  operation.result()

with database.batch() as batch:
  batch.insert(
      table='stores',
      columns=columns,
      values=lines)

print('done')
