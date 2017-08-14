#!/usr/bin/env perl 

# pipe to a shell.
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

    print "echo '***' $path\n";
    if (!$repo_seen_before{$repo}++) {
	print "if [ -d $repodir ]; then git -C $repodir remote update; else git clone --mirror $repo $repodir; fi\n";
    }
    print "if [ -d $path ]; then echo $path already cloned; else git clone $repodir $path; fi\n";
    print "git -C $path checkout $checkout\n";
    print "git -C $path pull\n";
}

__DATA__
# format:  directory_to_clone_to  url_to_clone_from  git_commit_id_to_checkout
# 
#modules/veraPDF-library-integration https://github.com/veraPDF/veraPDF-library integration
#modules/veraPDF-library-integration/parent/veraPDF-validation https://github.com/veraPDF/veraPDF-validation integration
modules/veraPDF-library-integration/parent/veraPDF-model https://github.com/vera#PDF/veraPDF-model integration
modules/veraPDF-library-integration/parent/veraPDF-parser https://github.com/veraPDF/veraPDF-parser integration
it/veraPDF-corpus https://github.com/veraPDF/veraPDF-corpus master



