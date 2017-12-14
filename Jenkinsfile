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

        stage('Build & Sign') {
            tools { 
                maven 'Maven3.3.9' 
            }
            steps {
                sh "mvn clean install -P sign"
            }
            post {
                success {
                    archive "**/lib/target/*.jar"
                    archive "**/lib/target/*.jar.asc"
                }
            }
        }

        stage('Unit Tests') {
            tools { 
                maven 'Maven3.3.9' 
            }
            steps {
                sh "mvn test"
            }
            post {
                always {
                    junit '**/lib/target/surefire-reports/TEST-*.xml'
                    step( [ $class: 'JacocoPublisher', execPattern: 'lib/target/jacoco.exec' ] )
                }
            }
        }

        stage('Integration Tests') {
            tools { 
                maven 'Maven3.3.9' 
            }
            steps {
                sh "mvn verify -P integration-test"
            }
        }

        stage('Deploy to OSS') {
            tools { 
                maven 'Maven3.3.9' 
            }
            steps {
                sh "cd lib && mvn clean deploy -P oss-deploy && cd .."
            }
        }
    }
}
