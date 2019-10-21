# Atlassian README

While this is a fork of the [offical maintenance branch of Jackson 1](https://github.com/FasterXML/jackson-1), it is
only forked so that we can deploy a release with all the latest fixes to the Atlassian 3rd Party Maven repository. As
currently there is no official plan to release a 1.9.14, and we require some of the fixes that have been backported from
the Jackson 2.x release.

The repository already contains Ant scripts for deploying to Maven using the Ant Maven Tasks, however these are deprecated
and don't appear to work correctly with maven.atlassian.com. To get around this a deployment script has been created that
uses Maven and the `deploy:deploy-file` Mojo directly.

## Deploying to maven.atlassian.com

* Set the `IMPL_VERSION` property in `build.xml` (eg. `<property name="IMPL_VERSION" value="1.9.13-atlassian-1" />`) 
* Build a release using `ant all`
* To deploy a snapshot run `bin/atlassian-deploy.sh snapshot`
* To deploy a release run `bin/atlassian-deploy.sh release`

The deployment script will verify that all required files are present before doing the deploy using 
`mvn deploy:deploy-file`.