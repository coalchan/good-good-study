#!/bin/bash

if [[ ! -d data ]]; then
    mkdir data
fi

cd data

curl -L -O http://files.grouplens.org/datasets/movielens/ml-1m.zip

unzip ml-1m.zip

rm -rf ml-1m.zip
