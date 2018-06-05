pipeline {
    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk'
    }
    stages {
        stage ('Initialize') {
            steps {
                echo "PATH = ${PATH}"
                echo "M2_HOME = ${M2_HOME}"
            }
        }

        stage ('Build') {
            steps {
                mvn install
            }
        }
      stage('Artifact') {
        steps {
          archiveArtifacts '*.jar'
        }
      }
    }
}
