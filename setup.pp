vcsrepo { imars_bin:
	source   => "git@github.com:USF-IMARS/imars_bin.git",
#	revision => 'production',  # aka branch
	ensure   => latest,
	provider => git,
	user     => 'root', #uses root's $HOME/.ssh setup
	owner    => $::ipopp::ipopp_user,
	group    => $::ipopp::ipopp_group,
	require  => File['/root/.ssh/id_rsa'],
}
