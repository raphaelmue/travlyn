pipeline {
    environment {
        registry = 'raphaelmue/travlyn'
        registryCredentials = 'dockerhub'

        OPENROUTE_API_KEY = credentials('openroute-api-key')
        DBPEDIA_API_KEY = credentials('dbpedia-api-key')
    }

    agent any

    tools {
        maven 'Maven 3.6.2'
        jdk 'JDK 11.0.1'
    }

    stages {
        stage('Prepare Build') {
            steps {
                sh 'cp $OPENROUTE_API_KEY java/org.travlyn.server/src/main/resources/OpenRouteKey.txt'
                sh 'cp $DBPEDIA_API_KEY java/org.travlyn.server/src/main/resources/DBpediaKey.txt'
            }
        }

        stage('Build') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java') {
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('android') {
                            sh 'chmod +x gradlew'
                            sh 'echo "sdk.dir=$JENKINS_HOME/android-sdk" >> local.properties'
                            sh './gradlew clean assembleDebug'
                            sh 'mv app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/debug/travlyn-debug.apk'
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: '**/*.apk', fingerprint: true
                        }
                    }
                }
            }
        }

        stage('Tests') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('android') {
                             sh 'chmod +x gradlew'
                             sh './gradlew test'
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = '$JENKINS_HOME/SonarQubeScanner'
            }
            steps {
                dir('java') {
                    sh 'cp target/jacoco.exec org.travlyn.server/target/'
                    sh 'cp target/jacoco.exec org.travlyn.shared/target/'
                    sh 'mvn dependency:copy-dependencies'
                    withSonarQubeEnv('SonarQubeServer') {
                        script {
                            if (env.CHANGE_ID) {
                                sh "${scannerHome}/bin/sonar-scanner " +
                                        "-Dsonar.pullrequest.base=master " +
                                        "-Dsonar.pullrequest.key=${env.CHANGE_ID} " +
                                        "-Dsonar.pullrequest.branch=${env.BRANCH_NAME} " +
                                        "-Dsonar.pullrequest.provider=github " +
                                        "-Dsonar.pullrequest.github.repository=raphaelmue/travlyn"
                            } else {
                                if (env.BRANCH_NAME != 'master') {
                                    sh "${scannerHome}/bin/sonar-scanner " +
                                            "-Dsonar.branch.name=${env.BRANCH_NAME} " +
                                            "-Dsonar.branch.target=master"
                                } else {
                                    sh "${scannerHome}/bin/sonar-scanner"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/TEST-*.xml'
            step([$class: 'JacocoPublisher'])
        }
    }
}
