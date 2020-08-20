/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//node-affinity
//nodes 1..3 are reserved for Jenkins slave pods.
//node 0 is used for the Jenkins master

timestamps {

    podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    metadata:
        name: dind
    spec:
      affinity:
        nodeAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 1
            preference:
              matchExpressions:
              - key: palisade-node-name
                operator: In
                values:
                - node1
                - node2
                - node3
      containers:
      - name: jnlp
        image: jenkins/jnlp-slave
        imagePullPolicy: Always
        args:
        - $(JENKINS_SECRET)
        - $(JENKINS_NAME)
        resources:
          requests:
            ephemeral-storage: "4Gi"
          limits:
            ephemeral-storage: "8Gi"

      - name: docker-cmds
        image: 779921734503.dkr.ecr.eu-west-1.amazonaws.com/jnlp-did:200608
        imagePullPolicy: IfNotPresent
        command:
        - sleep
        args:
        - 99d
        env:
          - name: DOCKER_HOST
            value: tcp://localhost:2375
        resources:
          requests:
            ephemeral-storage: "4Gi"
          limits:
            ephemeral-storage: "8Gi"

      - name: hadolint
        image: hadolint/hadolint:v1.18.0-6-ga0d655d-alpine@sha256:e0f960b5acf09ccbf092ec1e8f250cd6b5c9a586a0e9783c53618d76590b6aec
        imagePullPolicy: IfNotPresent
        command:
            - cat
        tty: true
        resources:
          requests:
            ephemeral-storage: "1Gi"
          limits:
            ephemeral-storage: "2Gi"

      - name: dind-daemon
        image: docker:1.12.6-dind
        imagePullPolicy: IfNotPresent
        resources:
          requests:
            cpu: 20m
            memory: 512Mi
        securityContext:
          privileged: true
        volumeMounts:
          - name: docker-graph-storage
            mountPath: /var/lib/docker
        resources:
          requests:
            ephemeral-storage: "1Gi"
          limits:
            ephemeral-storage: "2Gi"

      - name: maven
        image: 779921734503.dkr.ecr.eu-west-1.amazonaws.com/jnlp-dood-new-infra:200710
        imagePullPolicy: IfNotPresent
        command: ['docker', 'run', '-p', '80:80', 'httpd:latest']
        tty: true
        volumeMounts:
          - mountPath: /var/run
            name: docker-sock
        resources:
          requests:
            ephemeral-storage: "4Gi"
          limits:
            ephemeral-storage: "8Gi"

      volumes:
        - name: docker-graph-storage
          emptyDir: {}
        - name: docker-sock
          hostPath:
             path: /var/run

    ''') {
        node(POD_LABEL) {
            def GIT_BRANCH_NAME

            stage('Bootstrap') {
                if (env.CHANGE_BRANCH) {
                    GIT_BRANCH_NAME=env.CHANGE_BRANCH
                } else {
                    GIT_BRANCH_NAME=env.BRANCH_NAME
                }
                echo sh(script: 'env | sort', returnStdout: true)
            }

            stage('Prerequisites') {
                dir ('Palisade-common') {
                    git branch: 'develop', url: 'https://github.com/gchq/Palisade-common.git'
                    if (sh(script: "git checkout ${GIT_BRANCH_NAME}", returnStatus: true) == 0) {
                        container('docker-cmds') {
                            configFileProvider([configFile(fileId: "${env.CONFIG_FILE}", variable: 'MAVEN_SETTINGS')]) {
                                sh 'mvn -s $MAVEN_SETTINGS install -P quick'
                            }
                        }
                    }
                }
                dir ('Palisade-readers') {
                    git branch: 'develop', url: 'https://github.com/gchq/Palisade-readers.git'
                    if (sh(script: "git checkout ${GIT_BRANCH_NAME}", returnStatus: true) == 0) {
                        container('docker-cmds') {
                            configFileProvider([configFile(fileId: "${env.CONFIG_FILE}", variable: 'MAVEN_SETTINGS')]) {
                                sh 'mvn -s $MAVEN_SETTINGS install -P quick'
                            }
                        }
                    }
                }
                dir ('Palisade-clients') {
                    git branch: 'develop', url: 'https://github.com/gchq/Palisade-clients.git'
                    if (sh(script: "git checkout ${GIT_BRANCH_NAME}", returnStatus: true) == 0) {
                        container('docker-cmds') {
                            configFileProvider([configFile(fileId: "${env.CONFIG_FILE}", variable: 'MAVEN_SETTINGS')]) {
                                sh 'mvn -s $MAVEN_SETTINGS install -P quick'
                            }
                        }
                    }
                }
            }

            stage('Install, Unit Tests, Checkstyle') {
                dir ('Palisade-examples') {
                    git branch: GIT_BRANCH_NAME, url: 'https://github.com/gchq/Palisade-examples.git'
                    container('docker-cmds') {
                        configFileProvider([configFile(fileId: "${env.CONFIG_FILE}", variable: 'MAVEN_SETTINGS')]) {
                            sh 'mvn -s $MAVEN_SETTINGS install'
                        }
                    }
                }
            }
            stage('Hadolinting') {
                dir('Palisade-examples') {
                    container('hadolint') {
                        sh 'hadolint */Dockerfile'
                    }
                }
            }
            stage('SonarQube analysis') {
                dir ('Palisade-examples') {
                    container('docker-cmds') {
                        withCredentials([string(credentialsId: "${env.SQ_WEB_HOOK}", variable: 'SONARQUBE_WEBHOOK'),
                                         string(credentialsId: "${env.SQ_KEY_STORE_PASS}", variable: 'KEYSTORE_PASS'),
                                         file(credentialsId: "${env.SQ_KEY_STORE}", variable: 'KEYSTORE')]) {
                            configFileProvider([configFile(fileId: "${env.CONFIG_FILE}", variable: 'MAVEN_SETTINGS')]) {
                                withSonarQubeEnv(installationName: 'sonar') {
                                    if (env.CHANGE_BRANCH) {
                                        sh 'mvn -s $MAVEN_SETTINGS org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar -Dsonar.projectKey="Palisade-Examples-${CHANGE_BRANCH}" -Dsonar.projectName="Palisade-Examples-${CHANGE_BRANCH}" -Dsonar.webhooks.project=$SONARQUBE_WEBHOOK -Djavax.net.ssl.trustStore=$KEYSTORE -Djavax.net.ssl.trustStorePassword=$KEYSTORE_PASS'
                                    } else {
                                        sh 'mvn -s $MAVEN_SETTINGS org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar -Dsonar.projectKey="Palisade-Examples-${BRANCH_NAME}" -Dsonar.projectName="Palisade-Examples-${BRANCH_NAME}" -Dsonar.webhooks.project=$SONARQUBE_WEBHOOK -Djavax.net.ssl.trustStore=$KEYSTORE -Djavax.net.ssl.trustStorePassword=$KEYSTORE_PASS'
                                    }
                                }
                            }
                        }
                    }
                }
            }

            stage("SonarQube Quality Gate") {
                // Wait for SonarQube to prepare the report
                sleep(time: 10, unit: 'SECONDS')
                // Just in case something goes wrong, pipeline will be killed after a timeout
                timeout(time: 5, unit: 'MINUTES') {
                    // Reuse taskId previously collected by withSonarQubeEnv
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "Pipeline aborted due to SonarQube quality gate failure: ${qg.status}"
                    }
                }
            }

            stage('Maven deploy') {
                dir ('Palisade-examples') {
                    container('docker-cmds') {
                        configFileProvider([configFile(fileId: "${env.CONFIG_FILE}", variable: 'MAVEN_SETTINGS')]) {
                            if (("${env.BRANCH_NAME}" == "develop") ||
                                    ("${env.BRANCH_NAME}" == "master")) {
                                sh 'mvn -s $MAVEN_SETTINGS deploy -P quick'
                            } else {
                                sh "echo - no deploy"
                            }
                        }
                    }
                }
            }
        }
    }
}