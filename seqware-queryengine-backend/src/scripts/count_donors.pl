use strict;
use JSON qw(decode_json);
use LWP::Simple;
use Data::Dumper;

my ($input) = @ARGV;

open IN, "<$input" or die;

open OUT, ">problems.txt" or die;

my $curr_portal = "1";

my $count = 0;
while(<IN>) {

  $count++;
  print "COUNT $count\n";

  #print $_;

  chomp;
  next if (/MUTATION/);
  my @a = split /\t/;
  my $mid = $a[1];
  my $donor_cnt = $a[2];
  my @d = split /,/, $donor_cnt;
  my $total_donors = scalar(@d);
  my $proj_cnt = {};
  foreach my $tuple (@d) {
    my @dp = split /::/, $tuple;
    $proj_cnt->{$mid}{$dp[1]}++;
  }

  # now ask the DCC for the count
  if ($curr_portal eq "1") {
    $curr_portal = "2";
  } else {
    $curr_portal = "1";
  }

  #my $url = "http://hportal$curr_portal-dcc.oicr.on.ca:5381/api/mutations/projects/donors/counts?mutationIds=$mid&filters={%22mutation%22:{%22_mutation_id%22:[%22$mid%22]},%22donor%22:{%22_summary._available_data_type%22:[%22ssm%22]}}";
  my $url = "https://portal.dcc.icgc.org/api/mutations/projects/donors/counts?mutationIds=$mid&filters={%22mutation%22:{%22_mutation_id%22:[%22$mid%22]},%22donor%22:{%22_summary._available_data_type%22:[%22ssm%22]}}";

  #print "$url\n"; 

  my $json = decode_json(get ($url));

  foreach my $mid (keys %{$json}) {
    foreach my $proj (keys %{$json->{$mid}}) {
      if ($json->{$mid}{$proj} == $proj_cnt->{$mid}{$proj}) {
        #print "SAME\n";
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
  }
}

print "\n";

close IN;
close OUT;

