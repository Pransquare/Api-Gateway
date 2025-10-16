pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.53.39.170"
        SERVICE_NAME = "api-gateway"
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
                sshagent(['ec2-key']) {

                    // Create deployment directory if it doesn't exist
                    bat "ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} \"mkdir -p ${DEPLOY_DIR}\""

                    // Copy JAR to EC2
                    bat "scp -o StrictHostKeyChecking=no target\\${SERVICE_NAME}.jar ec2-user@${EC2_HOST}:${DEPLOY_DIR}/"

                    // Stop any existing instance
                    bat "ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} \"pkill -f ${SERVICE_NAME}.jar || true\""

                    // Start new instance in background
                    bat "ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} \"nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=8085 > ${DEPLOY_DIR}/${SERVICE_NAME}.log 2>&1 &\""
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment completed successfully!"
        }
        failure {
            echo "❌ Deployment failed. Check Jenkins console logs for details."
        }
    }
}
