#!/bin/sh

./run_ner -v czech-140205-cnec2.0.ner <cnec2.0-all/dtest.txt >czech-ner-140205-cnec2.0.dtest.entities
./compare_ne_outputs_v3.pl cnec2.0-all/dtest.all.entities czech-ner-140205-cnec2.0.dtest.entities
