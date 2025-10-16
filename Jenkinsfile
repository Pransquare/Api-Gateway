pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.53.39.170"
        SERVICE_NAME = "api-gateway"
        SERVER_PORT = "8085"
        LOG_FILE = "api-gateway.log"
        SSH_CREDENTIALS_ID = "ec2-ssh-key"
    }

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Pransquare/Api-Gateway.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    def mvnCmd = 'mvn clean package -DskipTests'
                    if (isUnix()) {
                        sh mvnCmd
                    } else {
                        bat mvnCmd
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    def remote = [
                        name: "ec2-server",
                        host: EC2_HOST,
                        user: "ec2-user",
                        allowAnyHosts: true,
                        credentialsId: SSH_CREDENTIALS_ID
                    ]

                    echo "===== Creating deploy directory on EC2 if not exists ====="
                    sshCommand remote: remote, command: "mkdir -p ${DEPLOY_DIR}"

                    echo "===== Copying JAR to EC2 ====="
                    sshPut remote: remote, from: "target/${SERVICE_NAME}.jar", into: "${DEPLOY_DIR}/"

                    echo "===== Stopping old instance (if running) ====="
                    sshCommand remote: remote, command: "pkill -f ${SERVICE_NAME}.jar || true"

                    echo "===== Starting new instance ====="
                    sshCommand remote: remote, command: """
                        nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar \
                        --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/${LOG_FILE} 2>&1 &
                    """

                    echo "===== Verifying process is running ====="
                    sshCommand remote: remote, command: "ps -ef | grep -v grep | grep ${SERVICE_NAME}.jar"

                    echo "===== Showing last 10 log lines ====="
                    sshCommand remote: remote, command: "tail -n 10 ${DEPLOY_DIR}/${LOG_FILE}"
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment completed successfully! ${SERVICE_NAME} is running on port ${SERVER_PORT}"
        }
        failure {
            echo "❌ Deployment failed. Check Jenkins console logs for details."
        }
    }
}
