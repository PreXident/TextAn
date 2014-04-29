#!/usr/bin/env perl

# Created from compare_ne_outputs_v2.pl by Milan Straka
# Last modification: 19 Nov 2013
# Changes: always compare all entities, show per-class results, remove confusion matrix code

# Reads sequence of N named entity recognizers outputs (filenames) and
# evaluates them to the first one.
#
# Usage:
# ------
#
# ./compare_ne_outputs.pl gold_filename system_filename_1 [system_filename_2 ... system_filename_N]
#
# Input format:
# -------------
#
# Tabulator separated columns, one line per entity. For each entity, the line is:
#
# <unique entity id>tab<entity class>tab<entity text>
#
# E.g.
#
# token-s12-w1  person  Pepa
# token-s13-w4  country Anglie
#
# Unique entity ids must (obviously) be shared between the gold and system
# output files, e.g. m-rf from PDT from the original file is a good choice. For
# multiword entities, unique entity id is composed of sorted comma separated
# concatenation of the corresponding word ids.
#
# E.g.
#
# token-s12-w1,token-s12-w2     P       Pepa Novak
# token-s13-w1,tokens13-w2      P       Ruzena Novakova
#
# It is possible to give more classifications for one word.
#
# E.g.
#
# token-s12-w1  location  Vltava
# token-s12-w1  person  Vltava
#
# Hierarchical (embedded) entities must be decomposed:
#
# <if Skody<gu Plzen>> =>
#
# id1,id2       if      Skody Plzen
# id2           gu      Plzen
#
# Evaluation rules:
# -----------------
#
# Both span and correct class(es) must guessed correctly for each named entity
# (= each line). When multiple classes exist in the gold data, each is
# considered a separate named entity (= multiple lines). Hierarchical
# (embedded) entities are decomposed into separate entities (=multiple lines)
# which are considered separately.
#
# Author:
# -------
#
# Jana Strakova <strakova@ufal.mff.cuni.cz>
#
# Copyright and licence:
# ----------------------
#
# Copyright © 2012 by Institute of Formal and Applied Linguistics, Charles University in Prague
#
# This module is free software; you can redistribute it and/or modify it under the same terms as Perl itself.

use strict;
use warnings;

$#ARGV >= 1 or die "Usage: compare_ne_recognizers.pl gold.filename system_filename_1 [system_filename_2 ... system_filename_N  ]";

our %id2name;

sub read_ne($$) {
    my ($filename, $ne_ref) = @_;
    open F,"<$filename" or die "Cannot open file $filename\n";
    while (<F>) {
        chomp;
        my ($ne, $class, $normalized_name) = split /\t/;
        $ne_ref->{$ne} = {} if not exists $ne_ref->{$ne};
        $ne_ref->{$ne}->{$class}=1;
        $id2name{$ne} = $normalized_name;
    }
    foreach my $ne (keys %{$ne_ref}) {
      $ne_ref->{$ne} = [keys %{$ne_ref->{$ne}}];
    }
}

sub get_subtypes(@) {
  my @subtypes = map { substr $_, 0, 1 } @_;
  my %seen;
  return grep { !$seen{$_}++ } @subtypes;
}

sub num_of_correct_types($$) {
    my ($gold_ref, $test_ref) = @_;

    my $num = 0;
    foreach my $t (@{$test_ref}) {
        foreach my $g (@{$gold_ref}) {
            $num++ if $t eq $g;
        }
    }
    return $num;
}

sub num_of_correct_suptypes($$) {
    my ($gold_ref, $test_ref) = @_;

    my $num = 0;
    foreach my $t (get_subtypes(@{$test_ref})) {
        foreach my $g (get_subtypes(@{$gold_ref})) {
            $num++ if $t eq $g;
        }
    }
    return $num;
}

sub is_member($$) {
    my ($member, $array_ref) = @_;
    foreach my $a (@{$array_ref}) {
        return 1 if $a eq $member;
    }
    return 0;
}

# read gold data
my %gold;
read_ne($ARGV[0], \%gold);

# read and compare evaluated data
foreach my $i (1..$#ARGV) {

    # read 
    my %entities;
    read_ne($ARGV[$i], \%entities);

    # compare

    # all entities
    my $correct = 0;                    # correct, that is number of nes in gold
    my $correct_subtype = 0;            # correct, that is number of subtypes in gold
    my $correct_span = 0;               # correct, that is number of spans in gold
    my $retrieved = 0;                  # retrieved, that is number of nes in test
    my $retrieved_subtype = 0;          # retrieved, that is number of subtypes in test
    my $retrieved_span = 0;             # retrieved, that is number of spans in test
    my $correct_retrieved = 0;          # correct retrieved type
    my $correct_retrieved_suptype = 0;  # correct retrieved supertype
    my $correct_retrieved_span = 0;     # correct retrieved span

    # oneword entities (same numbers restricted for oneword entities)
    my $correct_oneword = 0;
    my $correct_oneword_subtype = 0;
    my $correct_oneword_span = 0;
    my $retrieved_oneword = 0;
    my $retrieved_oneword_subtype = 0;
    my $retrieved_oneword_span = 0;
    my $correct_retrieved_oneword = 0;
    my $correct_retrieved_suptype_oneword = 0;
    my $correct_retrieved_span_oneword = 0;

    # twoword entities (same numbers restricted for twoword entities)
    my $correct_twoword = 0;
    my $correct_twoword_subtype = 0;
    my $correct_twoword_span = 0;
    my $retrieved_twoword = 0;
    my $retrieved_twoword_subtype = 0;
    my $retrieved_twoword_span = 0;
    my $correct_retrieved_twoword = 0;
    my $correct_retrieved_suptype_twoword = 0;
    my $correct_retrieved_span_twoword = 0;

    # count number of gold entities -- sum lists of classifications for each word
    foreach my $gold_ne (keys %gold) {
        $correct += @{$gold{$gold_ne}};
        $correct_subtype += get_subtypes(@{$gold{$gold_ne}});
        $correct_span++;
        if ($gold_ne !~ /,/) {
            $correct_oneword += @{$gold{$gold_ne}};
            $correct_oneword_subtype += get_subtypes(@{$gold{$gold_ne}});
            $correct_oneword_span++;
        }
        if ($gold_ne =~ /^[^,]+,[^,]+$/) {
            $correct_twoword += @{$gold{$gold_ne}};
            $correct_twoword_subtype += get_subtypes(@{$gold{$gold_ne}});
            $correct_twoword_span++;
        }
    }

    # count number of retrieved entities -- sum lists of classifications for each word
    foreach my $ne (keys %entities) {
        $retrieved += @{$entities{$ne}};
        $retrieved_subtype += get_subtypes(@{$entities{$ne}});
        $retrieved_span++;
        if ($ne !~ /,/) {
            $retrieved_oneword += @{$entities{$ne}};
            $retrieved_oneword_subtype += get_subtypes(@{$entities{$ne}});
            $retrieved_oneword_span++;
        }
        if ($ne =~ /^[^,]+,[^,]+$/) {
            $retrieved_twoword += @{$entities{$ne}};
            $retrieved_twoword_subtype += get_subtypes(@{$entities{$ne}});
            $retrieved_twoword_span++;
        }
    }

    # count number of correct retrieved entities
    foreach my $ne (keys %entities) {
        # all entities
        $correct_retrieved += num_of_correct_types($gold{$ne},$entities{$ne}) if exists $gold{$ne};
        $correct_retrieved_suptype += num_of_correct_suptypes($gold{$ne},$entities{$ne}) if exists $gold{$ne};
        $correct_retrieved_span++ if exists $gold{$ne};
        # oneword
        if ($ne !~ /,/) {             
            $correct_retrieved_oneword += num_of_correct_types($gold{$ne},$entities{$ne}) if exists $gold{$ne};
            $correct_retrieved_suptype_oneword += num_of_correct_suptypes($gold{$ne},$entities{$ne}) if exists $gold{$ne};
            $correct_retrieved_span_oneword++ if exists $gold{$ne};
        }
        # twoword
        if ($ne =~ /^[^,]+,[^,]+$/) {  
            $correct_retrieved_twoword += num_of_correct_types($gold{$ne},$entities{$ne}) if exists $gold{$ne};
            $correct_retrieved_suptype_twoword += num_of_correct_suptypes($gold{$ne},$entities{$ne}) if exists $gold{$ne};
            $correct_retrieved_span_twoword++ if exists $gold{$ne}; 
        }
    }

    # type (typ pojmenovane entity)

    my $recall = $correct ? $correct_retrieved / $correct : 0;
    my $precision = $retrieved ? $correct_retrieved / $retrieved : 0;
    my $f_measure = ( $recall + $precision ) ? (2 * $recall * $precision) / ( $recall + $precision) : 0;

    my $recall_oneword = $correct_oneword ? $correct_retrieved_oneword / $correct_oneword : 0;
    my $precision_oneword = $retrieved_oneword ? $correct_retrieved_oneword / $retrieved_oneword : 0;
    my $f_measure_oneword = ( $recall_oneword + $precision_oneword) ? (2 * $recall_oneword * $precision_oneword) / ( $recall_oneword + $precision_oneword) : 0;

    my ($recall_twoword, $precision_twoword, $f_measure_twoword) = (0, 1, 0);
    if ($correct_retrieved_twoword > 0 and $retrieved_twoword > 0) {
      $recall_twoword = $correct_retrieved_twoword / $correct_twoword;
      $precision_twoword = $correct_retrieved_twoword / $retrieved_twoword;
      $f_measure_twoword = (2 * $recall_twoword * $precision_twoword) / ( $recall_twoword + $precision_twoword);
    }

    # suptype (nadtyp pojmenovane entity)

    my $recall_suptype = $correct_subtype ? $correct_retrieved_suptype / $correct_subtype : 0;
    my $precision_suptype = $retrieved_subtype ? $correct_retrieved_suptype / $retrieved_subtype : 0;
    my $f_measure_suptype = ( $recall_suptype + $precision_suptype) ? (2 * $recall_suptype * $precision_suptype) / ( $recall_suptype + $precision_suptype) : 0;

    my $recall_suptype_oneword = $correct_oneword_subtype ? $correct_retrieved_suptype_oneword / $correct_oneword_subtype : 0;
    my $precision_suptype_oneword = $retrieved_oneword_subtype ? $correct_retrieved_suptype_oneword / $retrieved_oneword_subtype : 0;
    my $f_measure_suptype_oneword = ( $recall_suptype_oneword + $precision_suptype_oneword) ? (2 * $recall_suptype_oneword * $precision_suptype_oneword) / ( $recall_suptype_oneword + $precision_suptype_oneword) : 0;

    my ($recall_suptype_twoword, $precision_suptype_twoword, $f_measure_suptype_twoword) = (0, 1, 0);
    if ($correct_retrieved_suptype_twoword > 0 and $retrieved_twoword > 0) {
      $recall_suptype_twoword = $correct_retrieved_suptype_twoword / $correct_twoword_subtype;
      $precision_suptype_twoword = $correct_retrieved_suptype_twoword / $retrieved_twoword_subtype;
      $f_measure_suptype_twoword = (2 * $recall_suptype_twoword * $precision_suptype_twoword) / ( $recall_suptype_twoword + $precision_suptype_twoword);
    }

    # entity span recognition (rozsah pojmenovane entity)

    my $recall_span = $correct_span ? $correct_retrieved_span / $correct_span : 0;
    my $precision_span = $retrieved_span ? $correct_retrieved_span / $retrieved_span : 0;
    my $f_measure_span = ($recall_span + $precision_span) ? (2 * $recall_span * $precision_span) / ($recall_span + $precision_span) : 0;

    my $recall_span_oneword = $correct_oneword_span ? $correct_retrieved_span_oneword / $correct_oneword_span : 0;
    my $precision_span_oneword = $retrieved_oneword_span ? $correct_retrieved_span_oneword / $retrieved_oneword_span : 0;
    my $f_measure_span_oneword = ($recall_span_oneword + $precision_span_oneword) ? (2 * $recall_span_oneword * $precision_span_oneword) / ($recall_span_oneword + $precision_span_oneword) : 0;

    my ($recall_span_twoword, $precision_span_twoword, $f_measure_span_twoword) = (0, 1, 0);
    if ($correct_retrieved_span_twoword > 0 and $retrieved_twoword_span > 0) {
      $recall_span_twoword = $correct_retrieved_span_twoword / $correct_twoword_span;
      $precision_span_twoword = $correct_retrieved_span_twoword / $retrieved_twoword_span;
      $f_measure_span_twoword = (2 * $recall_span_twoword * $precision_span_twoword) / ($recall_span_twoword + $precision_span_twoword);
    }
   
    # print
    print "-------------\n";
    print "$ARGV[$i]\n";
    print "Gold entities: $correct (all), $correct_oneword (oneword), $correct_twoword (twoword)\n";
    print "Retrieved entities: $retrieved (all), $retrieved_oneword (oneword), $retrieved_twoword (twoword)\n";

    print "\n";
    print  "All entities (p/r/f)           | Oneword (p/r/f)       | Twoword (p/r/f)\n";

    printf "Type:   %6.2f /%6.2f /%6.2f |%6.2f /%6.2f /%6.2f |%6.2f /%6.2f /%6.2f\n", 
            $precision * 100, $recall * 100, $f_measure * 100, 
            $precision_oneword * 100, $recall_oneword * 100, $f_measure_oneword * 100,
            $precision_twoword * 100, $recall_twoword * 100, $f_measure_twoword * 100;
    printf "Suptype:%6.2f /%6.2f /%6.2f |%6.2f /%6.2f /%6.2f |%6.2f /%6.2f /%6.2f\n", 
            $precision_suptype * 100, $recall_suptype * 100, $f_measure_suptype * 100, 
            $precision_suptype_oneword * 100, $recall_suptype_oneword * 100, $f_measure_suptype_oneword * 100,
            $precision_suptype_twoword * 100, $recall_suptype_twoword * 100, $f_measure_suptype_twoword * 100;
    printf "Span:   %6.2f /%6.2f /%6.2f |%6.2f /%6.2f /%6.2f |%6.2f /%6.2f /%6.2f\n", 
            $precision_span * 100, $recall_span * 100, $f_measure_span * 100, 
            $precision_span_oneword * 100, $recall_span_oneword * 100, $f_measure_span_oneword * 100,
            $precision_span_twoword * 100, $recall_span_twoword * 100, $f_measure_span_twoword * 100;

    # detailed results for every supertype
    my %all_supertypes;

    my ($gold_total, %gold_counts) = 0;
    foreach my $g (keys %gold) {
      foreach my $c (get_subtypes(@{$gold{$g}})) { $gold_counts{$c}++; $all_supertypes{$c} = 1; }
    }

    my ($entities_total, %entities_counts) = 0;
    foreach my $e (keys %entities) {
      foreach my $c (get_subtypes(@{$entities{$e}})) { $entities_counts{$c}++; $all_supertypes{$c} = 1; }
    }

    my %correct_counts;
    foreach my $g (keys %gold) {
      if (exists $entities{$g}) {
        my @gold_sub = get_subtypes(@{$gold{$g}});
        my @entities_sub = get_subtypes(@{$entities{$g}});
        foreach my $c (@gold_sub) {
          $correct_counts{$c}++ if is_member($c, \@entities_sub);
        }
      }
    }

    foreach my $e (sort keys %all_supertypes) {
      my $precision = exists $gold_counts{$e} && exists $correct_counts{$e} ? $correct_counts{$e} / $entities_counts{$e} : 0;
      my $recall = exists $entities_counts{$e} && exists $correct_counts{$e} ? $correct_counts{$e} / $gold_counts{$e} : 0;
      my $fmeasure = $precision || $recall ? (2 * $precision * $recall) / ($precision + $recall) : 0;
      printf "%-8s%6.2f /%6.2f /%6.2f\n", "$e:", 100 * $precision, 100 * $recall, 100 * $fmeasure;
    }
}
