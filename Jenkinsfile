pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user"
        EC2_HOST = "13.60.47.188"
        SERVICE_NAME = "api-gateway"
        SERVER_PORT = "8085"
        LOG_FILE = "api-gateway.log"
        SSH_CREDENTIALS_ID = "ec2-linux-key" // Create SSH credentials in Jenkins
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
                    // Define the remote server
                    def remote = [:]
                    remote.name = 'ec2-server'
                    remote.host = env.EC2_HOST
                    remote.user = 'ec2-user'
                    remote.identityFile = 'C:\\ProgramData\\Jenkins\\.ssh\\krishna.pem'

                    // Upload JAR
                    sshPut remote: remote, from: "target\\${SERVICE_NAME}.jar", into: "${DEPLOY_DIR}/"

                    // Stop old instance and start new one safely
                    sshCommand remote: remote, command: """
                        echo "===== Stopping old API Gateway instance if running ====="
                        pgrep -f ${SERVICE_NAME}.jar && pkill -f ${SERVICE_NAME}.jar || echo "No running instance found"
                        
                        echo "===== Starting new API Gateway instance ====="
                        nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/${LOG_FILE} 2>&1 &
                        
                        echo "✅ Deployment completed successfully!"
                    """
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
