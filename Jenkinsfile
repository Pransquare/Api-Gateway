pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.53.39.170"
        SERVICE_NAME = "api-gateway"
        SSH_CREDENTIALS_ID = "ec2-ssh-key" // your Jenkins credential ID
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
                sh 'mvn clean package -DskipTests' // use sh if agent supports it, or bat if Windows
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['ec2-ssh-key']) {
                    sh """
                    echo ===== Creating deploy directory on EC2 if not exists =====
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "mkdir -p ${DEPLOY_DIR}"

                    echo ===== Copying JAR to EC2 =====
                    scp -o StrictHostKeyChecking=no target/${SERVICE_NAME}.jar ec2-user@${EC2_HOST}:${DEPLOY_DIR}/

                    echo ===== Stopping old API-Gateway instance if running =====
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "pkill -f ${SERVICE_NAME}.jar || true"

                    echo ===== Starting new API-Gateway instance =====
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=8085 > ${DEPLOY_DIR}/api-gateway.log 2>&1 &"

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
