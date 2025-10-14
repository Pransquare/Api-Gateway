pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user"
        SERVICE_NAME = "api-gateway"
        SERVER_PORT = "8085"
        LOG_FILE = "api-gateway.log"
        SSH_CREDENTIALS_ID = "ec2-linux-key"
        KNOWN_HOSTS_PATH = "C:\\ProgramData\\Jenkins\\.ssh\\known_hosts"
    }

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/Pransquare/Api-Gateway.git', branch: 'master'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    sshPut remote: [
                        host: '13.60.47.188',
                        user: 'ec2-user',
                        credentialsId: SSH_CREDENTIALS_ID,
                        knownHosts: readTrusted(KNOWN_HOSTS_PATH)
                    ],
                    from: "target\\${SERVICE_NAME}.jar",
                    into: "${DEPLOY_DIR}/"

                    sshCommand remote: [
                        host: '13.60.47.188',
                        user: 'ec2-user',
                        credentialsId: SSH_CREDENTIALS_ID,
                        knownHosts: readTrusted(KNOWN_HOSTS_PATH)
                    ],
                    command: """
                        if pgrep -f ${SERVICE_NAME}.jar > /dev/null; then
                            pkill -f ${SERVICE_NAME}.jar
                        else
                            echo "No running instance of ${SERVICE_NAME}.jar found."
                        fi
                    """

                    sshCommand remote: [
                        host: '13.60.47.188',
                        user: 'ec2-user',
                        credentialsId: SSH_CREDENTIALS_ID,
                        knownHosts: readTrusted(KNOWN_HOSTS_PATH)
                    ],
                    command: "nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/${LOG_FILE} 2>&1 &"
                }
            }
        }
    }

    post {
        failure {
            echo "❌ Deployment failed. Check Jenkins console logs for details."
        }
    }
}
