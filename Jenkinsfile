pipeline {
    agent any
    tools{
        maven 'maven 3'
        jdk 'java 8'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                echo "PATH = ${PATH}"
                echo "M2_HOME = ${M2_HOME}"
                mvn -Dmaven.repo.local=/home/andrew/.m2/repository clean install
                '''
            }
        }

        stage ('Build') {
            steps {
                sh '''
                mvn help:evaluate -Dexpression=settings.localRepository | grep -v [INFO]
                mvn install -o -e
                '''
            }
        }
      stage('Artifact') {
        steps {
          archiveArtifacts '*.jar'
        }
      }
    }
}
