#!/usr/bin/env perl 

while(<DATA>) {
    chomp;
    next if (/^ *#/); # comment line
    next if (/^ *$/); # blank line
    ($path, $repo, $checkout, $rest) = split(/[ \t]+/);
    if (defined $rest) {
	die "$path has extra\n";
    }
    # print "path = $path, repo = $repo, checkout = $checkout\n";
    $encoded_repo = $repo;
    $encoded_repo =~ s/([^a-z0-9])/"_".sprintf("%X",ord($1))/eg;
    $repodir = "git-mirrors/$encoded_repo";

    if (!$repo_seen_before{$repo}++) {
	print "if [ -d $repodir ]; then echo already mirrored - fetch; else git clone --mirror $repo $repodir; fi\n"; # fetch?
    }
    print "if [ -d $path ]; then echo already cloned; else git clone $repodir $path; fi\n"; # update?
    print "git -C $path checkout $checkout\n";
}

__DATA__
# format:  directory_to_clone_to  url_to_clone_from  git_commit_id_to_checkout
# 
