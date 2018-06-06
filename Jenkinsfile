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
                echo "M2_HOME = /home/andrew/.m2"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn install -o'
            }
        }
      stage('Artifact') {
        steps {
          archiveArtifacts '*.jar'
        }
      }
    }
}
