#!/usr/bin/env groovy

pipeline {
    agent any
    
    stages {
        stage('Preparation') {
            steps {
                deleteDir()
                git 'ssh://ci@91.121.149.68:29418/jee/padlock.git'
            }
        }

        stage('Unit Tests & Build') {
            tools { 
                maven 'Maven3.3.9' 
            }
            steps {
                sh "mvn --version"
                sh "mvn clean install"
            }
            post {
                success {
                    archive "**/target/*.jar"
                }
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    step( [ $class: 'JacocoPublisher' ] )
                }
            }
        }
    }
}
