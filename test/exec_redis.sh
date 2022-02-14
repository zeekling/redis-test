#!/bin/bash

function exec() {
	ip="$1"
    port="$2"
	file="$3"
	while read line
    do
	    echo "> $line"
        echo $line | redis-cli -h "$ip" -p "$port"
    done < $file
}

