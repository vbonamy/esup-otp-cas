DESTINATION=/home/ubuntu/workspace/cas/libs
SOURCE=/home/ubuntu/workspace/modules/esupOtpCas/build/libs/esupOtpCas-0.1.jar

function copy() {
	echo -e "Copying configuration files from $SOURCE to $DESTINATION"
	cp -rfv $SOURCE $DESTINATION/
}

case "$1" in
"deploy")
    copy
    ;;
*)
    help
    ;;
esac