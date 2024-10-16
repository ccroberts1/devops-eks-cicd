def gv

pipeline {   
    agent any
    tools {
        maven 'Maven'
    }
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("increment version") {
                    steps {
                        script {
                            gv.incrementVersion()
                        }
                    }
                }
        stage("build jar") {
            steps {
                script {
                    gv.buildJar()

                }
            }
        }

        stage("build image") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }

        stage("deploy") {
            environment {
                AWS_ACCESS_KEY_ID = credentials('jenkins_aws_access_key_id')
                AWS_SECRET_ACCESS_KEY = credentials('jenkins_aws_secret_access_key')
                APP_NAME = 'java-maven-app'
                DOCKER_REPO = 'ccroberts1'
            }
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
        stage("commit version update") {
            steps {
                script {
                    gv.commitVersionUpdate()
                }
            }
        }
    }
} 
