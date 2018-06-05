pipeline {
    agent any
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
