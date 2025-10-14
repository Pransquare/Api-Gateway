pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user"
        EC2_HOST = "13.60.47.188"
        SERVICE_NAME = "api-gateway"
        PEM_PATH = "C:\\ProgramData\\Jenkins\\.ssh\\krishna.pem"
        SERVER_PORT = "8085"
        LOG_FILE = "api-gateway.log"
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
                withEnv(["PATH=${tool 'Git'}/bin:${env.PATH}"]) {
                    bat """
                    echo ===== Copying JAR to EC2 =====
                    scp -i "${PEM_PATH}" -o StrictHostKeyChecking=no target\\${SERVICE_NAME}.jar ec2-user@${EC2_HOST}:${DEPLOY_DIR}/

                    echo ===== Stopping old API Gateway instance if running =====
                    ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "pkill -f ${SERVICE_NAME}.jar || true"

                    echo ===== Starting new API Gateway instance =====
                    ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/${LOG_FILE} 2>&1 &"

                    echo ✅ Deployment completed successfully!
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
