use strict;
use JSON qw(decode_json);
use LWP::Simple;
use Data::Dumper;

my ($input) = @ARGV;

open IN, "<$input" or die;

open OUT, ">count_donors_per_gene_problems.txt" or die;

my $curr_portal = "1";

my $count = 0;
while(<IN>) {

  $count++;
  if ($count % 10 == 0) {
    print "COUNT $count\n";
  }

  #print $_;

  chomp;
  next if (/GENE/);
  my @a = split /\t/;
  my $gid = $a[0];
  my $donor_cnt = $a[1];
  my @d = split /,/, $donor_cnt;
  my $total_donors = scalar(@d);
  # remove the duplicate donors
  my $proj_donor_cnt = {};
  foreach my $tuple (@d) {
    my @dp = split /::/, $tuple;
    $proj_donor_cnt->{$gid}{$dp[1]}{$dp[0]}++;
  }

  # final per project count with dup donors removed
  my $proj_cnt = {};
  foreach my $proj (keys %{$proj_donor_cnt->{$gid}}) {
    my $donor_count = scalar(keys %{$proj_donor_cnt->{$gid}{$proj}});
    $proj_cnt->{$gid}{$proj} = $donor_count;
  }



  my $url = "https://portal.dcc.icgc.org/api/genes/projects/donors/counts?geneIds=$gid&filters={%22gene%22:{%22_gene_id%22:[%22$gid%22]}}";

  #print "$url\n"; 

  my $json = decode_json(get ($url));

  #print "TOTAL MUT: $total_mutations\n";

  my $same = 1;

  # loop through projects
  foreach my $proj (keys %{$proj_cnt->{$gid}}) {
    if ($json->{$gid}{$proj} != $proj_cnt->{$gid}{$proj}) { $same = 0; }
  }

  if ($same) {
    #print "SAME $total_mutations vs ".$json->{$did}."\n";
  } else {
        print "DIFFERENT!\n";
        print OUT "DIFFERENT!\n";
        print "Portal\n";
        print OUT "Portal\n";
        print Dumper ($json);
        print OUT Dumper ($json);
        print "\nHBase\n";
        print OUT "\nHBase\n";
        print Dumper ($proj_cnt);
        print OUT Dumper ($proj_cnt);
        print "\n";
        print OUT "\n";
  }
}

print "\n";
close IN;
close OUT;

