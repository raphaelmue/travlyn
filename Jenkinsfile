pipeline {
    environment {
        registry = 'raphaelmue/travlyn'
        registryCredentials = 'dockerhub'
    }

    agent any

    tools {
        maven 'Maven 3.6.2'
        jdk 'JDK 11.0.1'
    }

    stages {
        stage('Build') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java')
                        'mvn clean install -DskipTests'
                    }
                }
                post {
                    always {
                        // archiveArtifacts artifacts: '**/*.msi, **/*.deb, **/*.dmg, **/*.apk', fingerprint: true
                    }
                }
            }
            stage('Android') {
                steps {
                    dir('android') {
                        // sh 'chmod +x gradlew'
                        // sh 'echo "sdk.dir=$JENKINS_HOME/android-sdk" >> local.properties'
                        // sh './gradlew clean assembleDebug'
                        // sh 'mv app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/debug/travlyn-debug.apk'
                    }
                }
                post {
                    always {
                        archiveArtifacts artifacts: '**/*.apk', fingerprint: true
                    }
                }
            }
        }

        stage('Unit Tests') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java') {
                            sh 'mvn test -P unit-tests'
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('android') {
                            // sh 'chmod +x gradlew'
                            // sh './gradlew test'
                        }
                    }
                }
            }
        }
        stage('Integration Tests') {
            steps {
                dir('java') {
                    sh 'mvn test -P integration-tests'
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = '$JENKINS_HOME/SonarQubeScanner'
            }
            steps {
                dir('java') {
                    // sh 'cp target/jacoco.exec org.travlyn.server/target/'
                    // sh 'mvn dependency:copy-dependencies'
                    // withSonarQubeEnv('SonarQubeServer') {
                    //     script {
                    //         if (env.CHANGE_ID) {
                    //             sh "${scannerHome}/bin/sonar-scanner " +
                    //                     "-Dsonar.pullrequest.base=master " +
                    //                     "-Dsonar.pullrequest.key=${env.CHANGE_ID} " +
                    //                     "-Dsonar.pullrequest.branch=${env.BRANCH_NAME} " +
                    //                     "-Dsonar.pullrequest.provider=github " +
                    //                     "-Dsonar.pullrequest.github.repository=raphaelmue/travlyn"
                    //         } else {
                    //             if (env.BRANCH_NAME != 'master') {
                    //                 sh "${scannerHome}/bin/sonar-scanner " +
                    //                         "-Dsonar.branch.name=${env.BRANCH_NAME} " +
                    //                         "-Dsonar.branch.target=master"
                    //             } else {
                    //                 sh "${scannerHome}/bin/sonar-scanner"
                    //             }
                    //         }
                    //     }
                    // }
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'deployment'
            }
            steps {

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
