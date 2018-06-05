pipeline {
  agent any
  stages {
    stage('Init') {
      steps {
        sh '''sh \'\'\'
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                \'\'\''''
      }
    }
    stage('Build') {
      steps {
        sh 'sh \'mvn install\''
      }
    }
    stage('Artifact') {
      steps {
        archiveArtifacts '*.jar'
      }
    }
  }
}