pipeline {
    agent {
        docker {
            image 'maven:3.8.6-openjdk-17'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec'
                }
            }
        }
        stage('Publish to Artifactory') {
            steps {
                script {
                    def server = Artifactory.server 'my-artifactory'
                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.tool = 'maven'
                    rtMaven.deployer server: server, releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local'
                    rtMaven.deployer.deployArtifacts = true
                    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install'
                    server.publishBuildInfo buildInfo
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t java-devops-demo:${BUILD_NUMBER} .'
                sh 'docker tag java-devops-demo:${BUILD_NUMBER} jfrog-artifactory:8082/docker-local/java-devops-demo:${BUILD_NUMBER}'
                sh 'docker push jfrog-artifactory:8082/docker-local/java-devops-demo:${BUILD_NUMBER}'
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                sh 'sed -i "s/{{VERSION}}/${BUILD_NUMBER}/g" kubernetes/deployment.yaml'
                sh 'kubectl apply -f kubernetes/'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}