#!/bin/sh

make html
scp -r build/html jlabl1:/userweb/gavalian/public_html/docs/tmd/.
