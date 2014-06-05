#!/bin/sh

./train_ner czech morphodita:czech-131112-pos_only.tagger features-tsd13.txt 2 30 -0.1 0.1 0.01 0.5 0 cnec2.0-all/dtest.txt <cnec2.0-all/train.txt >czech-140205-cnec2.0.ner
