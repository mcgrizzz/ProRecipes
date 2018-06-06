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
                mvn install -e
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
