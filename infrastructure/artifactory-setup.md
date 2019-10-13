# How to set up Artifactory server

```
docker pull docker.bintray.io/jfrog/artifactory-oss
docker run --name artifactory -d -p 8081:8081 docker.bintray.io/jfrog/artifactory-oss
```

## Login credentials

Navigate to http://localhost:8081/ and log in with the following credentials.

```
Username: admin
Password: password
```

## Create artifact repository

Go to Admin -> Repositories -> Local and then create a new local repository.
Place `pom.xml` and `settings.xml` at root of project on local machine, updating necessary fields to match the repository settings.
Deploy by using the command `mvn deploy --settings settings.xml`
