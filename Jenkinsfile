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
