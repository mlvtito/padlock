#!/usr/bin/env groovy

node {
   def mvnHome
   stage('Preparation') {
      deleteDir()
      git 'https://backend.r-w-x.net/scm/git/jee/padlock.git'
      mvnHome = tool 'Maven3.3.9'
      env.PATH = "${mvnHome}/bin:${env.PATH}"
   }
   withEnv(["PATH=${tool 'Maven3.3.9'}/bin:${PATH}"]) {
     stage('Install Dependencies') {
         sh "mvn clean install -DskipTests"
     }
     stage('Tests') {
        sh "mvn test"
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        step( [ $class: 'JacocoPublisher' ] )
     }
   }
}

